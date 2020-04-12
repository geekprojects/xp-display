/**
 * z737_fmc.c
 *
 * Created on: 20 août 2019
 *
 * This code is managing Zibo Mod FMC for Laminar Boeing 737-800
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
#include "qpac_msg.h"
#include "datarefs_z737.h"
#include "datarefs.h"
#include "z737_fmc.h"


/**
 * Structures & datarefs for CDU
 * We are using the Qpac MCDU packet to encode the data
 */
struct QpacMcduMsgLinesDataPacket zibo737CduMsgPacket;

// Store previous CDU packets to compare before sending
struct QpacMcduMsgLinesDataPacket zibo737Cdu1PreviousMsgPacket;
struct QpacMcduMsgLinesDataPacket zibo737Cdu2PreviousMsgPacket;

float zibo737_msg_delay;

/**
 * Structures & datarefs for FMS
 * We are using the Extended FMS packet to encode the data
 */
struct ExtendedFmsDataPacket	   z737_fmc_efms_packet[20];
int max_z737_fmc_efms_size = 0;

#define B738_LEG_STR_MAX_LEN 256

int createZibo737ExtendedFmsPackets(void) {

    char nav_id[64];
    char msg[80];
    int altitude;
    int speed;
    float lat;
    float lon;

    float ete;
    float gs;

    float fmc_lat[128];
    float fmc_lon[128];
    float fmc_speed[128];
    float fmc_alt[128];
    char str_leg[B738_LEG_STR_MAX_LEN];

    int displayed_entry;
    int active_entry;
    int total_waypoints;
    int displayed_waypoint = -1;
    int active_waypoint = -1;

     // the entry number in the FMS
    int cur_entry;

    // the same, but counting only non-zero entries
    // at the end, it should be equal to the number of non-zero entries
    int cur_waypoint = 0;

    // the entry number in the packet that we send
    int cur_packpoint = 0;

    int type;

    // index of current leg in laminar_B738_fms_legs
    char *str_leg_ptr;
    char *str_leg_save_ptr;
    // delimiter to split the legs names
    char str_leg_delim[] = " ";

    int i;

    int cur_pack = 0;

    // the actual number of waypoints, not counting zero'd waypoints
    total_waypoints = (int) XPLMGetDataf(laminar_B738_fms_num_of_wpts);

    XPLMGetDatavf(laminar_B738_fms_legs_lat, fmc_lat, 0, 128);
    XPLMGetDatavf(laminar_B738_fms_legs_lon, fmc_lon, 0, 128);
    XPLMGetDatavf(laminar_B738_fms_legs_spd, fmc_speed, 0, 128);
    XPLMGetDatavf(laminar_B738_fms_legs_alt_rest1, fmc_alt, 0, 128);
    // This is a string, space separated, of waypoints names;
    XPLMGetDatab(laminar_B738_fms_legs, str_leg, 0, B738_LEG_STR_MAX_LEN);

    if (total_waypoints > 0) {

        // sprintf(msg, "XHSI: Boeing 737-800 / Zibo Mod FMC: num_of_wpts=%d\n", total_waypoints);
        // XPLMDebugString(msg);

        // displayed_entry = (int) XPLMGetDatai(ufmc_waypt_pln_index);
        // active_entry = (int) XPLMGetDatai(ufmc_waypt_pln_index);
        displayed_entry = (int) XPLMGetDataf(laminar_B738_fms_legs_step_ctr);
        active_entry = 1;

        cur_entry = 1;
        //XPLMDebugString("XHSI: UFMC: cur_entry=");

        // Initiate leg string splitting
        str_leg_ptr = strtok(str_leg, str_leg_delim);

        while ( total_waypoints > 0
                && cur_waypoint < total_waypoints
                && cur_entry < MAX_EXTENDED_FMS_ENTRIES_ALLOWED ) {
/*
            sprintf(msg, " %d ", cur_entry);
            XPLMDebugString(msg);
*/
            cur_pack = (int)cur_waypoint / MAX_EXTENDED_FMS_ENTRIES_ALLOWED;

            altitude = fmc_alt[cur_waypoint];
            lat = fmc_lon[cur_waypoint];
            lon = fmc_lat[cur_waypoint];
            speed = fmc_speed[cur_waypoint];
            // XPLMGetDatab(ufmc_waypt_name,nav_id,0,sizeof(ufmc_efms_packet[cur_pack].entries[cur_packpoint].id));
            // type = XPLMGetDatai(ufmc_waypt_type_altitude);
            type = xplm_Nav_LatLon;

            // Get the waypoing name from laminar_B738_fms_legs string
        	if (str_leg_ptr != NULL) {
        		// Should we query the nav database to get the waypoint type ?
        		strncpy(nav_id, str_leg_ptr, 64);
        		type = xplm_Nav_Fix;
        	} else {
        		strncpy(nav_id, "UNKNOWN", 64);
        		type = xplm_Nav_LatLon;
        	}
        	str_leg_ptr = strtok(NULL, str_leg_delim);
        	// sprintf(msg, " %d %s", cur_entry, nav_id);
        	// XPLMDebugString(msg);
/*
            XPLMGetFMSEntryInfo(
                    cur_entry,
                    &type,
                    nav_id,
                    &outRef,
                    &altitude,
                    &lat,
                    &lon);
*/
            // only send non-zero entries
            if ( type != 255 ) {
                // if ( lat != 0.0f || lon != 0.0f ) {

                if (cur_entry == displayed_entry) {
                    // the corrected entry number for the displayed entry
                    displayed_waypoint = cur_waypoint;
                }
                if (cur_entry == active_entry) {
                    // the corrected entry number for the active entry
                    active_waypoint = cur_waypoint;
                }

                cur_packpoint = cur_waypoint % MAX_EXTENDED_FMS_ENTRIES_ALLOWED;

                z737_fmc_efms_packet[cur_pack].entries[cur_packpoint].type = custom_htoni( (int)type );
                if (type == xplm_Nav_LatLon) {
                    strncpy(z737_fmc_efms_packet[cur_pack].entries[cur_packpoint].id, "Lat/Lon", sizeof(z737_fmc_efms_packet[cur_pack].entries[cur_packpoint].id));
                } else {
                    strncpy(z737_fmc_efms_packet[cur_pack].entries[cur_packpoint].id, nav_id, sizeof(z737_fmc_efms_packet[cur_pack].entries[cur_packpoint].id));
                }
                z737_fmc_efms_packet[cur_pack].entries[cur_packpoint].altitude = custom_htoni(altitude);
                z737_fmc_efms_packet[cur_pack].entries[cur_packpoint].lat = custom_htonf(lat);
                z737_fmc_efms_packet[cur_pack].entries[cur_packpoint].lon = custom_htonf(lon);
                z737_fmc_efms_packet[cur_pack].entries[cur_packpoint].speed = custom_htoni(speed);

                // get ready for the next waypoint
                cur_waypoint++;
                // at the end, cur_waypoint will be the same as total_waypoints
            }
        cur_entry++;
        }
        /*
        XPLMDebugString("\n");
        sprintf(msg, "XHSI: Zibo 737 FMC: last=%d\n", cur_entry);
        XPLMDebugString(msg);
        sprintf(msg, "XHSI: Zibo 737 FMC: count=%d\n", cur_waypoint);
        XPLMDebugString(msg);
        */

        //    if ( ( total_waypoints > 1 ) && ( cur_waypoint != total_waypoints ) ) {
        //        sprintf(msg, "XHSI: Zibo 737 FMC: error count: %d %d\n", cur_waypoint, total_waypoints);
        //        XPLMDebugString(msg);
        //    }

    }

    ete = XPLMGetDataf(gps_dme_time_secs);
    gs = XPLMGetDataf(groundspeed);

    //    for (i = 0; i <= ((total_waypoints-1)/MAX_FMS_ENTRIES_POSSIBLE); i++) {
    for (i = 0; i <= cur_pack; i++) {
        strncpy(z737_fmc_efms_packet[i].packet_id, "FMS", 3);
        z737_fmc_efms_packet[i].packet_id[3] = '0' + (unsigned char)i;
        //sprintf(msg, "XHSI: Zibo 737 FMC filling %c%c%c%c \n",fms_packet[i].packet_id[0],fms_packet[i].packet_id[1],fms_packet[i].packet_id[2],fms_packet[i].packet_id[3]);
        //XPLMDebugString(msg);
        z737_fmc_efms_packet[i].ete_for_active = custom_htonf( ete );
        z737_fmc_efms_packet[i].groundspeed = custom_htonf( gs );
        z737_fmc_efms_packet[i].nb_of_entries = custom_htoni( total_waypoints );
        z737_fmc_efms_packet[i].displayed_entry_index = custom_htoni( displayed_waypoint );
        z737_fmc_efms_packet[i].active_entry_index = custom_htoni( active_waypoint );
    }

    // packet size : char[4] + float + float + int + int + int + ( # * ( int + char[8] + float + float + float ) )
    // return 24 + cur_waypoint * 24;
    // return the number of waypoints; let the sender sort it out...
    return total_waypoints;

}

float sendZibo737ExtendedFmsCallback(
									float	inElapsedSinceLastCall,
									float	inElapsedTimeSinceLastFlightLoop,
									int		inCounter,
									void *	inRefcon) {

	int i;
	int j;
	int waypoint_count;
	int packet_size;
	int last_pack;
	int res;
	int send_error = 0;
#if IBM
	char msg[80];
#endif


	if (xhsi_plugin_enabled && xhsi_send_enabled && xhsi_socket_open && fms_source==FMS_SOURCE_Z737 && z737_ready)  {

		waypoint_count = createZibo737ExtendedFmsPackets();
        last_pack = ( waypoint_count - 1 ) / MAX_FMS_ENTRIES_ALLOWED;

        for (j=0; j<=last_pack; j++) {

            if ( j == last_pack ) {
                packet_size = 24 + ( waypoint_count % MAX_FMS_ENTRIES_ALLOWED ) * 44;
            } else {
                packet_size = 24 + ( MAX_FMS_ENTRIES_ALLOWED ) * 44;
            }

            for (i=0; i<NUM_DEST; i++) {
                if (dest_enable[i]) {
                    res = sendto(sockfd, (const char*)&z737_fmc_efms_packet[j], packet_size, 0, (struct sockaddr *)&dest_sockaddr[i], sizeof(struct sockaddr));
#if IBM
                    if ( res == SOCKET_ERROR ) {
						send_error = 1;
                        XPLMDebugString("XHSI: caught error while sending ZiboMod FMCx packet! (");
                        sprintf(msg, "%d", WSAGetLastError());
                        XPLMDebugString(msg);
                        XPLMDebugString(")\n");
                    }
#else
                    if ( res < 0 ) {
						send_error = 1;
                        XPLMDebugString("XHSI: caught error while sending ZiboMod FMCx packet! (");
                        XPLMDebugString((char * const) strerror(GET_ERRNO));
                        XPLMDebugString(")\n");
                    }
#endif
                }
            }

        }

		if ( send_error )
			return 5.0f;
		else
			return fms_data_delay;

	} else {
		return 1.0f;
	}

}

/**
 * ZiboMod FMC Strings does not terminate with 0x00 but 0xd0
 * This function create a proper C String, 0x00 terminated
 * Ensuring that there will be not buffer overrun by limiting
 * string length to Z737_CDU_LINE_WIDTH
 * Return: String length
 */
int getZiboFMCString(char buffer[], XPLMDataRef fmcDataRef) {
	int datalen;
	int i;
	// Ensure that the buffer is filled by zero before grabbing the dataref content
	memset( buffer, '\0', Z737_CDU_LINE_WIDTH );
	// Grab the dataref
	datalen = XPLMGetDatab(fmcDataRef,buffer,0,Z737_CDU_LINE_WIDTH);
	// Ensure that the buffer is correctly 0 terminated
    buffer[Z737_CDU_LINE_WIDTH]=0;
    // Replace 0xd0 with 0x00
    i=0;
    while (buffer[i] != 0 && i < Z737_CDU_LINE_WIDTH) {
    	if (buffer[i] == 0xd0) buffer[i] = 0;
    	i++;
    }
    return (datalen > 0) ? (int)strlen(buffer) : 0;
}

void encodeZibo737CduTitleLine(int cdu_id, char *encoded_string) {
	int j;
	int p;
	char color = 'u';
	int small_len, white_len, inverted_len;
	int space = 0;
	char inverted_buffer[Z737_CDU_BUF_LEN];
	char white_buffer[Z737_CDU_BUF_LEN];
	char small_buffer[Z737_CDU_BUF_LEN];

	/*
	 *  Page title
	 */
	if (cdu_id) {
		white_len =    getZiboFMCString(white_buffer,    z737_fmc2_title_white);
		small_len =    getZiboFMCString(small_buffer,    z737_fmc2_title_small);
		inverted_len = getZiboFMCString(inverted_buffer, z737_fmc2_title_inverted);
	} else {
		white_len =    getZiboFMCString(white_buffer,    z737_fmc1_title_white);
		small_len =    getZiboFMCString(small_buffer,    z737_fmc1_title_small);
		inverted_len = getZiboFMCString(inverted_buffer, z737_fmc1_title_inverted);
	}

	color = 'u';
	memset( encoded_string, '\0', Z737_CDU_BUF_LEN );
	// encoded_string[0] = 0;
	space=0;
	for (j=0,p=0; (j<45) && (p<(Z737_CDU_BUF_LEN-9)); j++) {
		if ((j < white_len) && (white_buffer[j] != ' ') ) {
			if (color != 'W') {
				color = 'W';
				space=0;
				if (p>0) { encoded_string[p++] = ';'; }
				encoded_string[p++] = 'l';
				encoded_string[p++] = 'w';
				encoded_string[p++] = '0' + j/10;
				encoded_string[p++] = '0' + j%10;
			}
			if (white_buffer[j] < ' ') white_buffer[j] = '?';
			encoded_string[p++]= white_buffer[j];
		} else if ((j < small_len) && (small_buffer[j] != ' ') ) {
			if (color != 'w') {
				color = 'w';
				space=0;
				if (p>0) { encoded_string[p++] = ';'; }
				encoded_string[p++] = 's';
				encoded_string[p++] = 'w';
				encoded_string[p++] = '0' + j/10;
				encoded_string[p++] = '0' + j%10;
			}
			if (small_buffer[j] < ' ') small_buffer[j] = '?';
			encoded_string[p++]= small_buffer[j];
		} else if ((j < inverted_len) && (inverted_buffer[j] != ' ') ) {
			if (color != 'I') {
				color = 'I';
				space=0;
				if (p>0) { encoded_string[p++] = ';'; }
				encoded_string[p++] = 'l';
				encoded_string[p++] = 'i';
				encoded_string[p++] = '0' + j/10;
				encoded_string[p++] = '0' + j%10;
			}
			if (inverted_buffer[j] < ' ') inverted_buffer[j] = '?';
			encoded_string[p++]= inverted_buffer[j];
		} else if (( (j < white_len) || (j<small_len) || (j<inverted_len) )
				&& (space<2) ) {
			encoded_string[p++]=' ';
			space++;
		} else if (space>1) {
			color = 'u';
		} else {
			// color = 'u';
			break;
		}

	}
	encoded_string[p] = 0;
}

void encodeZibo737CduScratchPadLine(int cdu_id, char *encoded_string) {
	int p;
	int white_len, inverted_len;
	char inverted_buffer[Z737_CDU_BUF_LEN];
	char white_buffer[Z737_CDU_BUF_LEN];

	if (cdu_id) {
		inverted_len =  getZiboFMCString(inverted_buffer,  z737_fmc2_scratch_inverted);
		white_len =   getZiboFMCString(white_buffer,   z737_fmc2_scratch_white);
	} else {
		inverted_len =  getZiboFMCString(inverted_buffer,  z737_fmc1_scratch_inverted);
		white_len =   getZiboFMCString(white_buffer,   z737_fmc1_scratch_white);
	}

	memset( encoded_string, '\0', Z737_CDU_BUF_LEN );
	p=0;
	if ((white_len>0) && (white_buffer[0] > 32)) {
		encoded_string[p++] = 'l';
		encoded_string[p++] = 'w';
		encoded_string[p++] = '0';
		encoded_string[p++] = '0';
		strcpy(&encoded_string[p], white_buffer);
	} else if ((inverted_len>0) && (inverted_buffer[0] > 32)) {
		encoded_string[p++] = 'l';
		encoded_string[p++] = 'i';
		encoded_string[p++] = '0';
		encoded_string[p++] = '0';
		strcpy(&encoded_string[p], inverted_buffer);
	}
}

/**
 * cdu_id should be 0 for left, 1 for right. Any value different from 0 is treated as right
 */
int createZibo737CduPacket(int cdu_id) {

   int i,l;
   int j;
   int p;
   char color = 'u';
   int label_len, small_len, white_len, inverted_len, magenta_len, green_len;
   int space = 0;
   int status = 0;
   char label_buffer[Z737_CDU_BUF_LEN];
   char inverted_buffer[Z737_CDU_BUF_LEN];
   char white_buffer[Z737_CDU_BUF_LEN];
   char small_buffer[Z737_CDU_BUF_LEN];
   char magenta_buffer[Z737_CDU_BUF_LEN];
   char green_buffer[Z737_CDU_BUF_LEN];

   char encoded_string[Z737_CDU_BUF_LEN];

   /*
    * Packet header
    */
   memset( &zibo737CduMsgPacket, 0, sizeof(zibo737CduMsgPacket));
   strncpy(zibo737CduMsgPacket.packet_id, "QPAM", 4);
   zibo737CduMsgPacket.nb_of_lines = custom_htoni(Z737_CDU_LINES);
   zibo737CduMsgPacket.side = custom_htoni(cdu_id);
   if (cdu_id) {
	   status =
			   (XPLMGetDataf(z737_fmc_exec_lights_fo)!=0.0 ? MCDU_FLAG_EXEC : 0) |
			   (XPLMGetDataf(z737_fmc_message)!=0.0 ? MCDU_FLAG_MSG : 0);
   } else {
	   status =
			   (XPLMGetDataf(z737_fmc_exec_lights)!=0.0 ? MCDU_FLAG_EXEC : 0 ) |
			   (XPLMGetDataf(z737_fmc_message)!=0.0 ? MCDU_FLAG_MSG : 0);
   }
   zibo737CduMsgPacket.status = custom_htoni(status);

   // Line count (0 to 13)
   l=0;


   /*
    *  Page title
    */

   encodeZibo737CduTitleLine(cdu_id, zibo737CduMsgPacket.lines[l].linestr);
   zibo737CduMsgPacket.lines[l].len = custom_htoni((int)strlen(zibo737CduMsgPacket.lines[l].linestr));
   zibo737CduMsgPacket.lines[l].lineno = custom_htoni(l);
   l++;

   /*
    * Label & content lines
    */

   for(i=0; i<Z737_FMC_LINES; i++){

	   if (cdu_id) {
		   label_len =     getZiboFMCString(label_buffer,    z737_fmc2_label_white[i]);
		   white_len =     getZiboFMCString(white_buffer,    z737_fmc2_content_white[i]);
		   small_len =     getZiboFMCString(small_buffer,    z737_fmc2_content_small[i]);
		   inverted_len =  getZiboFMCString(inverted_buffer, z737_fmc2_content_inverted[i]);
		   magenta_len =   getZiboFMCString(magenta_buffer,  z737_fmc2_content_magenta[i]);
		   green_len =     getZiboFMCString(green_buffer,    z737_fmc2_content_green[i]);
	   } else {
		   label_len =     getZiboFMCString(label_buffer,    z737_fmc1_label_white[i]);
		   white_len =     getZiboFMCString(white_buffer,    z737_fmc1_content_white[i]);
		   small_len =     getZiboFMCString(small_buffer,    z737_fmc1_content_small[i]);
		   inverted_len =  getZiboFMCString(inverted_buffer, z737_fmc1_content_inverted[i]);
		   magenta_len =   getZiboFMCString(magenta_buffer,  z737_fmc1_content_magenta[i]);
		   green_len =     getZiboFMCString(green_buffer,    z737_fmc1_content_green[i]);
	   }

	   /*
	    * Label line
	    */
	   color = 'u';
	   memset( encoded_string, '\0', Z737_CDU_BUF_LEN );
	   // encoded_string[0] = 0;
	   space = 0;

	   for (j=0,p=0; (j<45) && (p<(Z737_CDU_BUF_LEN-9)); j++) {
		   if ((j < label_len) && (label_buffer[j] > 32)) {
			   if (color != 'w') {
				   color = 'w';
				   space = 0;
				   if (p>0) { encoded_string[p++] = ';'; }
				   encoded_string[p++] = 's';
				   encoded_string[p++] = color;
				   encoded_string[p++] = '0' + j/10;
				   encoded_string[p++] = '0' + j%10;
			   }
			   encoded_string[p++] = label_buffer[j];
		   } else if (((j < label_len)) && (space<2) ) {
			   encoded_string[p++] = ' ';
			   space++;
		   } else if (space>1) {
			   color = 'u';
		   } else {
			   // color = 'u';
			   break;
		   }
	   }

	   encoded_string[p] = 0;

	   strcpy(zibo737CduMsgPacket.lines[l].linestr,encoded_string);
	   zibo737CduMsgPacket.lines[l].len = custom_htoni(p);
	   zibo737CduMsgPacket.lines[l].lineno = custom_htoni(l);
	   l++;


	   /*
	    * Content line
	    */

	   color = 'u';
	   memset( encoded_string, '\0', Z737_CDU_BUF_LEN );
	   // encoded_string[0] = 0;
	   space=0;
	   for (j=0,p=0; (j<45) && (p<(Z737_CDU_BUF_LEN-9)); j++) {
		   if ((j < white_len) && (white_buffer[j] != ' ') ) {
			   if (color != 'W') {
				   color = 'W';
				   space = 0;
				   if (p>0) { encoded_string[p++] = ';'; }
				   encoded_string[p++] = 'l';
				   encoded_string[p++] = 'w';
				   encoded_string[p++] = '0' + j/10;
				   encoded_string[p++] = '0' + j%10;
			   }
			   if (white_buffer[j] < ' ') white_buffer[j] = '?';
			   encoded_string[p++] = white_buffer[j];
		   } else if ((j < small_len) && (small_buffer[j] != ' ') ) {
			   if (color != 'w') {
				   color = 'w';
				   space = 0;
				   if (p>0) { encoded_string[p++] = ';'; }
				   encoded_string[p++] = 's';
				   encoded_string[p++] = 'w';
				   encoded_string[p++] = '0' + j/10;
				   encoded_string[p++] = '0' + j%10;
			   }
			   if (small_buffer[j] < ' ') small_buffer[j] = '?';
			   encoded_string[p++] = small_buffer[j];
		   }  else if ((j < inverted_len) && (inverted_buffer[j] != ' ') ) {
			   if (color != 'I') {
				   color = 'I';
				   space = 0;
				   if (p>0) { encoded_string[p++] = ';'; }
				   encoded_string[p++] = 'l';
				   encoded_string[p++] = 'i';
				   encoded_string[p++] = '0' + j/10;
				   encoded_string[p++] = '0' + j%10;
			   }
			   if (inverted_buffer[j] < ' ') inverted_buffer[j] = '?';
			   encoded_string[p++] = inverted_buffer[j];
		   } else if ((j < magenta_len) && (magenta_buffer[j] != ' ') ) {
			   if (color != 'M') {
				   color = 'M';
				   space = 0;
				   if (p>0) { encoded_string[p++] = ';'; }
				   encoded_string[p++] = 'l';
				   encoded_string[p++] = 'm';
				   encoded_string[p++] = '0' + j/10;
				   encoded_string[p++] = '0' + j%10;
			   }
			   if (magenta_buffer[j] < ' ') magenta_buffer[j] = '?';
			   encoded_string[p++] = magenta_buffer[j];
		   } else if ((j < green_len) && (green_buffer[j] != ' ') ) {
			   if (color != 'G') {
				   color = 'G';
				   space = 0;
				   if (p>0) { encoded_string[p++] = ';'; }
				   encoded_string[p++] = 'l';
				   encoded_string[p++] = 'g';
				   encoded_string[p++] = '0' + j/10;
				   encoded_string[p++] = '0' + j%10;
			   }
			   if (green_buffer[j] < ' ') green_buffer[j] = '?';
			   encoded_string[p++] = green_buffer[j];
		   } else if (( (j < white_len) || (j<small_len) || (j<inverted_len) || (j<magenta_len) || (j<green_len))
				   && (space<2) ) {
			   encoded_string[p++]=' ';
			   space++;
		   } else if (space>1) {
			   color = 'u';
		   } else {
			   // color = 'u';
			   break;
		   }

	   }
	   encoded_string[p] = 0;
	   strcpy(zibo737CduMsgPacket.lines[l].linestr,encoded_string);
	   zibo737CduMsgPacket.lines[l].len = custom_htoni(p);
	   zibo737CduMsgPacket.lines[l].lineno = custom_htoni(l);
	   l++;

   }

   /*
    * Scratch pad line
    */

   encodeZibo737CduScratchPadLine(cdu_id, zibo737CduMsgPacket.lines[l].linestr);
   zibo737CduMsgPacket.lines[l].lineno = custom_htoni(l);
   zibo737CduMsgPacket.lines[l].len = custom_htoni((int)strlen(zibo737CduMsgPacket.lines[l].linestr));

   return 4 + 4 + Z737_CDU_LINES * 88;
}

int isZibo737CduUpdated(int cdu_pilot, int cdu_copilot) {
	char cdu1_title_line[Z737_CDU_BUF_LEN];
	char cdu1_scratch_line[Z737_CDU_BUF_LEN];
	char cdu2_title_line[Z737_CDU_BUF_LEN];
	char cdu2_scratch_line[Z737_CDU_BUF_LEN];
	int result=0;

	if (cdu_pilot == 0 || cdu_copilot == 0) {
		// Get current title and scratch lines
		encodeZibo737CduTitleLine(0, cdu1_title_line);
		encodeZibo737CduScratchPadLine(0, cdu1_scratch_line);
		// Compare with stored version
		result |= strncmp(cdu1_title_line,zibo737Cdu1PreviousMsgPacket.lines[0].linestr,Z737_CDU_BUF_LEN);
		result |= strncmp(cdu1_scratch_line,zibo737Cdu1PreviousMsgPacket.lines[13].linestr,Z737_CDU_BUF_LEN);
	}
	if (cdu_pilot == 1 || cdu_copilot == 1) {
		// Get current title and scratch lines
		encodeZibo737CduTitleLine(1, cdu2_title_line);
		encodeZibo737CduScratchPadLine(1, cdu2_scratch_line);
		// Compare with stored version
		result |= strncmp(cdu2_title_line,zibo737Cdu2PreviousMsgPacket.lines[0].linestr,Z737_CDU_BUF_LEN);
		result |= strncmp(cdu2_scratch_line,zibo737Cdu2PreviousMsgPacket.lines[13].linestr,Z737_CDU_BUF_LEN);
	}
	return result;
}

float sendZibo737MsgCallback(
									float	inElapsedSinceLastCall,
									float	inElapsedTimeSinceLastFlightLoop,
									int		inCounter,
									void *	inRefcon) {

	int i;
	int cdu_packet_size;
	int cdu_pilot;
	int cdu_copilot;
	int data_changed;

	z737_msg_count++;

	if (xhsi_plugin_enabled && xhsi_send_enabled && xhsi_socket_open && z737_cdu_ready) {
		cdu_pilot = XPLMGetDatai(cdu_pilot_side);
		cdu_copilot = XPLMGetDatai(cdu_copilot_side);
		data_changed = isZibo737CduUpdated(cdu_pilot,cdu_copilot);
		if (data_changed || (z737_fmc_keypressed > 0) || (z737_msg_count > Z737_MAX_MSG_COUNT))  {

			z737_msg_count=0;
			if (z737_fmc_keypressed>0) z737_fmc_keypressed--;

			cdu_packet_size = createZibo737CduPacket(cdu_pilot);
			if ( cdu_packet_size > 0 ) {
				for (i=0; i<NUM_DEST; i++) {
					if (dest_enable[i]) {
						if (sendto(sockfd, (const char*)&zibo737CduMsgPacket, cdu_packet_size, 0, (struct sockaddr *)&dest_sockaddr[i], sizeof(struct sockaddr)) == -1) {
							XPLMDebugString("XHSI: caught error while sending Zibo737McduMsg left CDU packet! (");
							XPLMDebugString((char * const) strerror(GET_ERRNO));
							XPLMDebugString(")\n");
						}
					}
				}
				if (cdu_pilot==0)
					zibo737Cdu1PreviousMsgPacket = zibo737CduMsgPacket;
				else
					zibo737Cdu2PreviousMsgPacket = zibo737CduMsgPacket;
			}


			if (cdu_pilot != cdu_copilot) {
				cdu_packet_size = createZibo737CduPacket(cdu_copilot);
				if ( cdu_packet_size > 0 ) {
					for (i=0; i<NUM_DEST; i++) {
						if (dest_enable[i]) {
							if (sendto(sockfd, (const char*)&zibo737CduMsgPacket, cdu_packet_size, 0, (struct sockaddr *)&dest_sockaddr[i], sizeof(struct sockaddr)) == -1) {
								XPLMDebugString("XHSI: caught error while sending Zibo737McduMsg right CDU packet! (");
								XPLMDebugString((char * const) strerror(GET_ERRNO));
								XPLMDebugString(")\n");
							}
						}
					}
				}
				if (cdu_copilot==0)
					zibo737Cdu1PreviousMsgPacket = zibo737CduMsgPacket;
				else
					zibo737Cdu2PreviousMsgPacket = zibo737CduMsgPacket;
			}
		}
        return cdu_data_delay;

	} else {
		// return 10.0f;
		return cdu_data_delay;
	}

}
