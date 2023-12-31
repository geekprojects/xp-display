/**
 * xp11_cdu.c
 *
 * Created on: 25 août 2019
 *
 * This code is X-Plane 11.35 CDU
 * https://developer.x-plane.com/article/datarefs-for-the-cdu-screen/
 *
 * Copyright (C) 2014-2019  Nicolas Carel
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
#include "xp11_cdu.h"
#include "qpac_msg.h"
#include "datarefs_z737.h"
#include "jar_a320neo_msg.h"

/**
 * Structures & datarefs for CDU
 * We are using the Qpac MCDU packet to encode the data
 */
struct xp11CduMsgLinesDataPacket xp11CduMsgPacket;

// Store previous CDU packets to compare before sending
struct xp11CduMsgLinesDataPacket xp11Cdu1PreviousMsgPacket;
struct xp11CduMsgLinesDataPacket xp11Cdu2PreviousMsgPacket;

int xp11_cdu_msg_count = 0;
int xp11_fms_keypressed = 0;
int xp11_cdu_total_line_len = 0; // to test if CDU data is totally blank i.e. CDU not ready

char color_codes[]="nbrygmawmmmmmmmm";

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
int getXP11FMSString(char signed_buffer[], XPLMDataRef fmcDataRef) {
	int datalen;
	int i;
	int shift;
	unsigned char *buffer;
	buffer = (unsigned char *)signed_buffer;
	// Ensure that the buffer is filled by zero before grabbing the dataref content
	memset( buffer, '\0', XP11_CDU_BUF_LEN );
	// Grab the dataref
	datalen = XPLMGetDatab(fmcDataRef,buffer,0,XP11_CDU_TEXT_LINE_WIDTH);

    // Translate some unicode characters
    i=0;
    shift=0;

    while ( (buffer[i] != 0) && (i < XP11_CDU_TEXT_LINE_WIDTH)) {
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

char getXP11FMSFontCode(int style_code) {
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
char getXP11FMSColorCode(int style_code) {
	int reverse_video = (style_code >> 6) & 0x01;
	char color_code =color_codes[style_code & 0x0F];
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

int encodeXP11CduLine(int cdu_id, int line, char *encoded_string) {
	int j;
	int p;
	char style;
	int text_len, style_len;
	int space = 0;
	char text_buffer[XP11_CDU_BUF_LEN];
	char style_buffer[XP11_CDU_BUF_LEN];

	// Ensure both buffers are filled with 0
	memset( encoded_string, '\0', XP11_CDU_BUF_LEN );
	memset( style_buffer, '\0', XP11_CDU_LINE_WIDTH );

	if (cdu_id) {
		text_len = getXP11FMSString(text_buffer, fms_cdu2_text[line]);
		style_len = XPLMGetDatab(fms_cdu2_style[line],style_buffer,0,XP11_CDU_LINE_WIDTH);
	} else {
		text_len = getXP11FMSString(text_buffer, fms_cdu1_text[line]);
		style_len = XPLMGetDatab(fms_cdu1_style[line],style_buffer,0,XP11_CDU_LINE_WIDTH);
	}

	style = 0xFF;
	space=0;

	for (j=0,p=0; (j<45) && (p<(XP11_CDU_BUF_LEN-9)); j++) {
		if ((j < text_len) && (text_buffer[j] != ' ') ) {
			if (style != style_buffer[j]) {
				style = style_buffer[j];
				space = 0;
				if (p>0) { encoded_string[p++] = ';'; }
				encoded_string[p++] = getXP11FMSFontCode(style);
				encoded_string[p++] = getXP11FMSColorCode(style);
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

int createXP11CduPacket(int cdu_id) {
   int i;
   int status = 0;
   int line_len;

   /*
    * Packet header
    */
   memset( &xp11CduMsgPacket, 0, sizeof(xp11CduMsgPacket));
   strncpy(xp11CduMsgPacket.packet_id, "QPAM", 4);
   xp11CduMsgPacket.nb_of_lines = custom_htoni(XP11_FMS_LINES);
   xp11CduMsgPacket.side = custom_htoni(cdu_id);
   xp11CduMsgPacket.status = custom_htoni(status);

   xp11_cdu_total_line_len = 0;
   for(i=0; i<XP11_FMS_LINES; i++){
	   line_len=encodeXP11CduLine(cdu_id, i, xp11CduMsgPacket.lines[i].linestr);
	   xp11_cdu_total_line_len += line_len;
       xp11CduMsgPacket.lines[i].len = custom_htoni(line_len);
       xp11CduMsgPacket.lines[i].lineno = custom_htoni(i);
   }

   return 4 + 4 + XP11_FMS_LINES * 88;
}

int isXP11CduUpdated(int cdu_pilot, int cdu_copilot) {
	char cdu1_title_line[XP11_CDU_BUF_LEN];
	char cdu1_scratch_line[XP11_CDU_BUF_LEN];
	char cdu2_title_line[XP11_CDU_BUF_LEN];
	char cdu2_scratch_line[XP11_CDU_BUF_LEN];
	int result = 0;

	if (cdu_pilot == 0 || cdu_copilot == 0) {
		// Get current title and scratch lines
		encodeXP11CduLine(0, 0, cdu1_title_line);
		encodeXP11CduLine(0, 13, cdu1_scratch_line);
		// Compare with stored version
		result |= strncmp(cdu1_title_line,xp11Cdu1PreviousMsgPacket.lines[0].linestr,XP11_CDU_BUF_LEN);
		result |= strncmp(cdu1_scratch_line,xp11Cdu1PreviousMsgPacket.lines[13].linestr,XP11_CDU_BUF_LEN);
	}
	if (cdu_pilot == 1 || cdu_copilot == 1) {
		// Get current title and scratch lines
		encodeXP11CduLine(1, 0, cdu2_title_line);
		encodeXP11CduLine(1, 13, cdu2_scratch_line);
		// Compare with stored version
		result |= strncmp(cdu2_title_line,xp11Cdu2PreviousMsgPacket.lines[0].linestr,XP11_CDU_BUF_LEN);
		result |= strncmp(cdu2_scratch_line,xp11Cdu2PreviousMsgPacket.lines[13].linestr,XP11_CDU_BUF_LEN);
	}
	return result;
}

int isXP11CduReady() {
	char cdu1_title_line[XP11_CDU_BUF_LEN];
	char cdu1_scratch_line[XP11_CDU_BUF_LEN];
	char cdu2_title_line[XP11_CDU_BUF_LEN];
	char cdu2_scratch_line[XP11_CDU_BUF_LEN];
	int len_cdu1_title_line;
	int len_cdu1_scratch_line;
	int len_cdu2_title_line;
	int len_cdu2_scratch_line;
	int result = 0;

	// Get current title and scratch lines from CDU1
	len_cdu1_title_line = encodeXP11CduLine(0, 0, cdu1_title_line);
	len_cdu1_scratch_line = encodeXP11CduLine(0, 13, cdu1_scratch_line);

	// Get current title and scratch lines from CDU2
	len_cdu2_title_line = encodeXP11CduLine(1, 0, cdu2_title_line);
	len_cdu2_scratch_line = encodeXP11CduLine(1, 13, cdu2_scratch_line);

	// Check line length
	result = (len_cdu1_title_line > 0) || (len_cdu1_scratch_line > 0) || (len_cdu2_title_line > 0) || (len_cdu2_scratch_line > 0);
	return result;
}

float sendXP11CduMsgCallback(
									float	inElapsedSinceLastCall,
									float	inElapsedTimeSinceLastFlightLoop,
									int		inCounter,
									void *	inRefcon) {

	int i;
	int cdu_packet_size;
	int cdu_pilot;
	int cdu_copilot;
	int data_changed;

	xp11_cdu_msg_count++;

	if (xhsi_plugin_enabled && xhsi_send_enabled && xhsi_socket_open && xp11_cdu_ready
			&& !qpac_mcdu_ready && !z737_cdu_ready && !jar_a320_mcdu_ready ) {

		cdu_pilot = XPLMGetDatai(cdu_pilot_side);
		cdu_copilot = XPLMGetDatai(cdu_copilot_side);
		data_changed = isXP11CduUpdated(cdu_pilot,cdu_copilot);

		if ( data_changed || (xp11_fms_keypressed > 0) || (xp11_cdu_msg_count > XP11_CDU_MAX_MSG_COUNT))  {
			xp11_cdu_msg_count=0;
			if (xp11_fms_keypressed>0) xp11_fms_keypressed--;

			cdu_packet_size = createXP11CduPacket(XPLMGetDatai(cdu_pilot_side));
			if ( cdu_packet_size > 0 ) {
				for (i=0; i<NUM_DEST; i++) {
					if (dest_enable[i]) {
						if (sendto(sockfd, (const char*)&xp11CduMsgPacket, cdu_packet_size, 0, (struct sockaddr *)&dest_sockaddr[i], sizeof(struct sockaddr)) == -1) {
							XPLMDebugString("XHSI: caught error while sending XP11 built in left CDU packet! (");
							XPLMDebugString((char * const) strerror(GET_ERRNO));
							XPLMDebugString(")\n");
						}
					}
				}
				if (XPLMGetDatai(cdu_pilot_side)==0)
					xp11Cdu1PreviousMsgPacket = xp11CduMsgPacket;
				else
					xp11Cdu2PreviousMsgPacket = xp11CduMsgPacket;
			}
			if (XPLMGetDatai(cdu_pilot_side) != XPLMGetDatai(cdu_copilot_side)) {
				cdu_packet_size = createXP11CduPacket(XPLMGetDatai(cdu_copilot_side));
				if ( cdu_packet_size > 0 ) {
					for (i=0; i<NUM_DEST; i++) {
						if (dest_enable[i]) {
							if (sendto(sockfd, (const char*)&xp11CduMsgPacket, cdu_packet_size, 0, (struct sockaddr *)&dest_sockaddr[i], sizeof(struct sockaddr)) == -1) {
								XPLMDebugString("XHSI: caught error while sending XP11 built in, right CDU packet! (");
								XPLMDebugString((char * const) strerror(GET_ERRNO));
								XPLMDebugString(")\n");
							}
						}
					}
				}
				if (XPLMGetDatai(cdu_copilot_side)==0)
					xp11Cdu1PreviousMsgPacket = xp11CduMsgPacket;
				else
					xp11Cdu2PreviousMsgPacket = xp11CduMsgPacket;
			}
		}
		return cdu_data_delay;
	} else {
		// XP11 CDU is not ready or preempted by a custom CDU
		// xp11_cdu_ready = isXP11CduReady();
		return 10.0f;
	}

}
