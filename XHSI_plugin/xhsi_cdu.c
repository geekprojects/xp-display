/**
 * xhsi_cdu.c
 *
 * Created on: 28 sept 2021
 *
 * This code is XHSI Internal CDU
 * XHSI Internal CDU provides basic CDU pages when no other CDU is available
 * Availables pages :
 * - RAD/NAV Pagexp11_cdu.c
 *
 *
 * Copyright (C) 2021  Nicolas Carel
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

#include <stdio.h>
#include <string.h>

#define XPLM200 1

// mingw-64 demands that winsock2.h is loaded before windows.h
#if IBM
#include <winsock2.h>
#endif


#include "XPLMDataAccess.h"
#include "XPLMPlugin.h"
#include "XPLMUtilities.h"
#include "XPLMNavigation.h"


#include "structs.h"

#include "endianess.h"
#include "plugin.h"
#include "globals.h"
#include "settings.h"
#include "net.h"
#include "datarefs.h"
#include "xhsi_cdu.h"
#include "qpac_msg.h"
#include "datarefs_z737.h"
#include "jar_a320neo_msg.h"

/**
 * Structures & datarefs for CDU
 * We are using the Qpac MCDU packet to encode the data
 */
struct xhsiCduMsgLinesDataPacket xhsiCduMsgPacket;

// Store previous CDU packets to compare before sending
struct xhsiCduMsgLinesDataPacket xhsiCdu1PreviousMsgPacket;
struct xhsiCduMsgLinesDataPacket xhsiCdu2PreviousMsgPacket;

int xhsi_cdu_msg_count = 0;
int xhsi_fms_keypressed = 0;
int xhsi_cdu_total_line_len = 0; // to test if CDU data is totally blank i.e. CDU not ready

char xhsi_cdu_color_codes[]="nbrygmawmmmmmmmm";

/**
 * X-Plane 11 CDU Strings are UTF-8 encoded.
 * 16 Lines with 24 characters per line.
 * This function create a proper C String, 0x00 terminated
 * Ensuring that there will be not buffer overrun by limiting
 * string length
 * X-Plane CDU Translation rules :
 *   Note that one character might need more than one byte of the dataref to display.
 *   You are expected to be able to read at least the following UTF-8 characters:
 *
 *   U+00B0 (degree sign): (0xC2 0xB0)
 *   U+2610 (ballot box): (0xE2 0x98 0x90)
 *   U+2190 (left arrow): (0xE2 0x86 0x90) to U+2193 (downwards arrow): (0xE2 0x86 0x93)
 *   U+0394 (greek capital letter delta): (0xCE 0x94)
 *   U+2B21 (white hexagon): (0xE2 0xAC 0xA1)
 *
 * XHSI Application Translation rules:
 * -----------------------------------
 *			case '`' : c = '°'; break;
 *			case '|' : c = 'Δ'; break;
 *			case '*' : c = '⎕'; break;
 *			case '0' : c = 'O'; break;
 *			case 0x1C : c = '←'; break; // U2190
 *			case 0x1D : c = '↑'; break; // U2191
 *			case 0x1E : c = '→'; break; // U2192
 *			case 0x1F : c = '↓'; break; // U2193
 * More special characters might be added in future versions.
 * Return: String length
 */
int getXHSIFMSString(char signed_buffer[], XPLMDataRef fmcDataRef) {
	int datalen;
	int i;
	int shift;
	unsigned char *buffer;
	buffer = (unsigned char *)signed_buffer;
	// Ensure that the buffer is filled by zero before grabbing the dataref content
	memset( buffer, '\0', XHSI_CDU_BUF_LEN );
	// Grab the dataref
	datalen = XPLMGetDatab(fmcDataRef,buffer,0,XHSI_CDU_TEXT_LINE_WIDTH);

    // Translate some unicode characters
    i=0;
    shift=0;

    while ( (buffer[i] != 0) && (i < XHSI_CDU_TEXT_LINE_WIDTH)) {
    	if (buffer[i] < 0x80 ) {
    		// this is standard ASCII one byte
    		if (shift>0) buffer[i-shift]=buffer[i];
    	} else if ( (buffer[i] >= 0x80) &&  (buffer[i] <= 0xBF) ) {
    		// this is a multibyte sequence
    		buffer[i-shift]='?';
    		i++;
    		shift++;
    	} else if ( (buffer[i] >= 0xC2) && (buffer[i] <= 0xDF) ) {
    		// this is a 2 byte sequence
    		if ( (buffer[i] == 0xC2) && (buffer[i+1] == 0xB0)) {
    			// degree sign
    			buffer[i-shift]='`';
    		} else if ( (buffer[i] == 0xCE) && (buffer[i+1] == 0x94) ) {
    			// delta sign
    			buffer[i-shift]='|';
    		} else {
    			buffer[i-shift]='%';
    		}
    		i++;
    		shift++;
    	} else if ( (buffer[i] >= 0xE0) && (buffer[i] <= 0xEF) ) {
    		// this is a 3 byte sequence
    		if ( (buffer[i] == 0xE2) && (buffer[i+1] == 0x98) && (buffer[i+2] == 0x90) ) {
    			// this is ballot box
    			buffer[i-shift]='*';
    		} else if ( (buffer[i] == 0xE2) && (buffer[i+1] == 0x86) && (buffer[i+2] == 0x90) ) {
    			// this is left arrow
    			buffer[i-shift]=0x1C;
    		} else if ( (buffer[i] == 0xE2) && (buffer[i+1] == 0x86) && (buffer[i+2] == 0x91) ) {
    			// this is left arrow
    			buffer[i-shift]=0x1D;
    		} else if ( (buffer[i] == 0xE2) && (buffer[i+1] == 0x86) && (buffer[i+2] == 0x92) ) {
    			// this is right arrow
    			buffer[i-shift]=0x1E;
    		} else if ( (buffer[i] == 0xE2) && (buffer[i+1] == 0x86) && (buffer[i+2] == 0x93) ) {
    			// this is left arrow
    			buffer[i-shift]=0x1F;
    		} else {
    			buffer[i-shift]='@';
    		}
    		i+=2;
    		shift+=2;
    	} else {
    		// 0xF0 to 0xFF this is a 4 byte sequence
    		buffer[i-shift]='#';
    		i+=3;
    		shift+=3;
    	}
    	i++;

    }
    buffer[i-shift]=0; // Terminate string

    return (datalen > 0) ? (int)strlen(signed_buffer) : 0;
}

char getXHSIFMSFontCode(int style_code) {
	return (style_code & 0x80) != 0 ? 'l' : 's' ;
}

/**
 * XHSI QPAM packet color code
 * n: Black (n is for French translation NOIR)
 * b: Blue
 * y: Yellow
 * m: magenta
 * a: amber
 * g: green
 * r: red
 * c: cyan - not yet implemented
 * Color code in lower case : normal color
 * Color code in upper case : reverse video color (text color will be black)
 */
char getXHSIFMSColorCode(int style_code) {
	int reverse_video = (style_code >> 6) & 0x01;
	char color_code = xhsi_cdu_color_codes[style_code & 0x0F];
	// remove 32 if Reverse video - i.e. convert in upper case
	if (reverse_video) color_code -= 32;
	return color_code;
}

/*
 * Decode style info
 * The formatting information for each character in the line as one byte (unsigned char) with the following special meaning :
 *   The highest bit is set for a text displayed in large font. So use mask (1<<7) for the bit that tells you large vs small font.
 *   The second highest bit is set for a text displayed in reverse video (colored background, black text). So use mask (1<<6) for the bit that tells you to invert the colors.
 *   The third highest bit is set for a text displayed flashing (text being turned an and off periodically). So use mask (1<<5) for the bit that tells you to flash.
 *   The fourth highest bit is set for a text with an underscore. So use mask (1<<4) for the bit that tells you to display an underscore under the character.
 *   The remaining four bits encode the color of the text (or the background for reverse video):
 *    BLACK(0),CYAN(1),RED(2),YELLOW(3),GREEN(4),MAGENTA(5),AMBER(6),WHITE(7).
 */

int encodeXHSICduLine(int cdu_id, int line, char *encoded_string) {
	int j;
	int p;
	char style;
	int text_len, style_len;
	int space = 0;
	char text_buffer[XHSI_CDU_BUF_LEN];
	char style_buffer[XHSI_CDU_BUF_LEN];

	// Ensure both buffers are filled with 0
	memset( encoded_string, '\0', XHSI_CDU_BUF_LEN );
	memset( style_buffer, '\0', XHSI_CDU_LINE_WIDTH );

	if (cdu_id) {
		text_len = getXHSIFMSString(text_buffer, fms_cdu2_text[line]);
		style_len = XPLMGetDatab(fms_cdu2_style[line],style_buffer,0,XHSI_CDU_LINE_WIDTH);
	} else {
		text_len = getXHSIFMSString(text_buffer, fms_cdu1_text[line]);
		style_len = XPLMGetDatab(fms_cdu1_style[line],style_buffer,0,XHSI_CDU_LINE_WIDTH);
	}

	style = 0xFF;
	space=0;

	for (j=0,p=0; (j<45) && (p<(XHSI_CDU_BUF_LEN-9)); j++) {
		if ((j < text_len) && (text_buffer[j] != ' ') ) {
			if (style != style_buffer[j]) {
				style = style_buffer[j];
				space = 0;
				if (p>0) { encoded_string[p++] = ';'; }
				encoded_string[p++] = getXHSIFMSFontCode(style);
				encoded_string[p++] = getXHSIFMSColorCode(style);
				encoded_string[p++] = '0' + j/10;
				encoded_string[p++] = '0' + j%10;
			}
			// if (text_buffer[j] < ' ') text_buffer[j] = '?';
			encoded_string[p++] = text_buffer[j];
		} else if ( (j < text_len)  && (space<2) ) {
			encoded_string[p++]=' ';
			space++;
		} else if (space>1) {
			style = 0xFF;
		} else {
			// color = 'u';
			break;
		}

	}
	encoded_string[p] = 0;
	return p;
}

int createXHSICduPacket(int cdu_id) {
   int i;
   int status = 0;
   int line_len;

   /*
    * Packet header
    */
   memset( &xhsiCduMsgPacket, 0, sizeof(xhsiCduMsgPacket));
   strncpy(xhsiCduMsgPacket.packet_id, "QPAM", 4);
   xhsiCduMsgPacket.nb_of_lines = custom_htoni(XP11_FMS_LINES);
   xhsiCduMsgPacket.side = custom_htoni(cdu_id);
   xhsiCduMsgPacket.status = custom_htoni(status);

   xhsi_cdu_total_line_len = 0;
   for(i=0; i<XP11_FMS_LINES; i++){
	   line_len=encodeXHSICduLine(cdu_id, i, xhsiCduMsgPacket.lines[i].linestr);
	   xhsi_cdu_total_line_len += line_len;
	   xhsiCduMsgPacket.lines[i].len = custom_htoni(line_len);
	   xhsiCduMsgPacket.lines[i].lineno = custom_htoni(i);
   }

   return 4 + 4 + XP11_FMS_LINES * 88;
}

int isXHSICduUpdated(int cdu_pilot, int cdu_copilot) {
	char cdu1_title_line[XHSI_CDU_BUF_LEN];
	char cdu1_scratch_line[XHSI_CDU_BUF_LEN];
	char cdu2_title_line[XHSI_CDU_BUF_LEN];
	char cdu2_scratch_line[XHSI_CDU_BUF_LEN];
	int result = 0;

	if (cdu_pilot == 0 || cdu_copilot == 0) {
		// Get current title and scratch lines
		encodeXHSICduLine(0, 0, cdu1_title_line);
		encodeXHSICduLine(0, 13, cdu1_scratch_line);
		// Compare with stored version
		result |= strncmp(cdu1_title_line,xhsiCdu1PreviousMsgPacket.lines[0].linestr,XHSI_CDU_BUF_LEN);
		result |= strncmp(cdu1_scratch_line,xhsiCdu1PreviousMsgPacket.lines[13].linestr,XHSI_CDU_BUF_LEN);
	}
	if (cdu_pilot == 1 || cdu_copilot == 1) {
		// Get current title and scratch lines
		encodeXHSICduLine(1, 0, cdu2_title_line);
		encodeXHSICduLine(1, 13, cdu2_scratch_line);
		// Compare with stored version
		result |= strncmp(cdu2_title_line,xhsiCdu2PreviousMsgPacket.lines[0].linestr,XHSI_CDU_BUF_LEN);
		result |= strncmp(cdu2_scratch_line,xhsiCdu2PreviousMsgPacket.lines[13].linestr,XHSI_CDU_BUF_LEN);
	}
	return result;
}

/**
 * XHSI Internal CDU is always ready
 */
int isXHSICduReady() {
	return -1;
}

float sendXHSICduMsgCallback(
									float	inElapsedSinceLastCall,
									float	inElapsedTimeSinceLastFlightLoop,
									int		inCounter,
									void *	inRefcon) {

	int i;
	int cdu_packet_size;
	int cdu_pilot;
	int cdu_copilot;
	int data_changed;

	xhsi_cdu_msg_count++;

	if (xhsi_plugin_enabled && xhsi_send_enabled && xhsi_socket_open && xp11_cdu_ready
			&& !qpac_mcdu_ready && !z737_cdu_ready && !jar_a320_mcdu_ready ) {

		cdu_pilot = XPLMGetDatai(cdu_pilot_side);
		cdu_copilot = XPLMGetDatai(cdu_copilot_side);
		data_changed = isXHSICduUpdated(cdu_pilot,cdu_copilot);

		if ( data_changed || (xhsi_fms_keypressed > 0) || (xhsi_cdu_msg_count > XHSI_CDU_MAX_MSG_COUNT))  {
			xhsi_cdu_msg_count=0;
			if (xhsi_fms_keypressed>0) xhsi_fms_keypressed--;

			cdu_packet_size = createXHSICduPacket(XPLMGetDatai(cdu_pilot_side));
			if ( cdu_packet_size > 0 ) {
				for (i=0; i<NUM_DEST; i++) {
					if (dest_enable[i]) {
						if (sendto(sockfd, (const char*)&xhsiCduMsgPacket, cdu_packet_size, 0, (struct sockaddr *)&dest_sockaddr[i], sizeof(struct sockaddr)) == -1) {
							XPLMDebugString("XHSI: caught error while sending XHSI internal left CDU packet! (");
							XPLMDebugString((char * const) strerror(GET_ERRNO));
							XPLMDebugString(")\n");
						}
					}
				}
				if (XPLMGetDatai(cdu_pilot_side)==0)
					xhsiCdu1PreviousMsgPacket = xhsiCduMsgPacket;
				else
					xhsiCdu2PreviousMsgPacket = xhsiCduMsgPacket;
			}
			if (XPLMGetDatai(cdu_pilot_side) != XPLMGetDatai(cdu_copilot_side)) {
				cdu_packet_size = createXHSICduPacket(XPLMGetDatai(cdu_copilot_side));
				if ( cdu_packet_size > 0 ) {
					for (i=0; i<NUM_DEST; i++) {
						if (dest_enable[i]) {
							if (sendto(sockfd, (const char*)&xhsiCduMsgPacket, cdu_packet_size, 0, (struct sockaddr *)&dest_sockaddr[i], sizeof(struct sockaddr)) == -1) {
								XPLMDebugString("XHSI: caught error while sending XHSI internal right CDU packet! (");
								XPLMDebugString((char * const) strerror(GET_ERRNO));
								XPLMDebugString(")\n");
							}
						}
					}
				}
				if (XPLMGetDatai(cdu_copilot_side)==0)
					xhsiCdu1PreviousMsgPacket = xhsiCduMsgPacket;
				else
					xhsiCdu2PreviousMsgPacket = xhsiCduMsgPacket;
			}
		}
		return cdu_data_delay;
	} else {
		// XHSI CDU is not ready or preempted by a custom CDU
		// xhsi_cdu_ready = isXP11CduReady();
		return 10.0f;
	}

}
