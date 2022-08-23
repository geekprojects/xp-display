/*
 * datarefs_aircrafts.c
 *
 * Goal : using only one call back to check each A/C datarefs
 * New method to detect A/C change while X-Plane is running
 *
 * Created on: 16 janv. 2022
 * Author: Nicolas Carel
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
#include <stdlib.h>
#include <stdint.h>


#define XPLM200 1

#include "XPLMProcessing.h"
#include "XPLMDataAccess.h"
#include "XPLMUtilities.h"
#include "XPLMNavigation.h"
#include "XPLMDisplay.h"
#include "XPLMMenus.h"
#include "XPLMPlugin.h"

#include "globals.h"
#include "ids.h"
#include "structs.h"
#include "settings.h"
#include "datarefs.h"
#include "datarefs_ufmc.h"
#include "datarefs_x737.h"
#include "datarefs_z737.h"
#include "datarefs_cl30.h"
#include "datarefs_q400.h"
#include "datarefs_pilotedge.h"
#include "datarefs_qpac.h"
#include "qpac_msg.h"
#include "datarefs_pa_a320.h"
#include "datarefs_jar_a320neo.h"
#include "datarefs_ff_a320.h"
#include "datarefs_xdual.h"
#include "datarefs_x_raas.h"
#include "xfmc.h"
#include "ufmc.h"
#include "endianess.h"

/*
 * Used to monitor the aircraft registration and ICAO Type
 * if any change, then reset aircraft datarefs to find them from scratch
 */
char aircraft_registration[40] = { 0,0,0,0,0,  0,0,0,0,0,  0,0,0,0,0,  0,0,0,0,0 };
char aircraft_icao_type[40]    = { 0,0,0,0,0,  0,0,0,0,0,  0,0,0,0,0,  0,0,0,0,0 };


/**
 * get aircraft registration from sim_aircraft_tailnum dataref
 * registration must be at least 40 char length
 */
void getAircraftRegistration( char * registration) {
	XPLMGetDatab(acf_tailnum, registration, 0, 40);
}

/**
 *get aircraft ICAO type from sim_aircraft_icao dataref
 * icao_type must be at least 40 char length
 */
void getAircraftICAOType(char * icao_type) {
	XPLMGetDatab(acf_icao, icao_type, 0, 40);
}


/**
 * Check aircrafts and other plugins datarefs every 5 seconds
 */
float checkPluginsDatarefsCallback(
        float	inElapsedSinceLastCall,
        float	inElapsedTimeSinceLastFlightLoop,
        int		inCounter,
        void *	inRefcon) {

	char current_registration[40];
	char current_icao_type[40];

	/*
	 * Check Aircraft Tail registration number & ICAO type
	 * if any change, then reset aircraft datarefs to find them from scratch
	 */
	getAircraftRegistration( current_registration );
	getAircraftICAOType( current_icao_type );
	if ( strncmp( aircraft_registration, current_registration, 40 ) != 0 || strncmp( aircraft_icao_type, current_icao_type, 40 ) != 0 ) {
		XPLMDebugString("XHSI: Aircraft changed - reseting A/C datarefs\n");

		// Force datarefs rescan for all plugins
		// Keep alphabetical source file order
		cl30_ready = 0;
		ff_a320_ready = 0;
		jar_a320_neo_ready=0;
		pa_a320_ready = 0;
		pilotedge_ready = 0;
		q400_ready = 0;
		qpac_ewd_ready = 0;
		qpac_mcdu_ready = 0;
		qpac_ready=0;
		ufmc_ready = 0;
		x_raas_ready = 0;
		x737_ready = 0;
		xdual_ready = 0;
		z737_ready = 0;

		strncpy(aircraft_registration, current_registration, 40);
		strncpy(aircraft_icao_type, current_icao_type, 40);
	}

	// Keep alphabetical source file order
    findCL30DataRefs();
    findFlightFactorA320DataRefs();
    findJarA320NeoDataRefs();
    findPaA320DataRefs();
    findPilotEdgeDataRefs();
    findQ400DataRefs();
    findQpacDataRefs();
    findXRaasDataRefs();
    findX737DataRefs();
    findXDualDataRefs();
    findZibo737DataRefs();
    // CDUs
    findQpacMsgDataRefs();
    findUFMCDataRefs();
    // findXfmcDataRefs();

    // come back in 5 sec
    return 5.0;
}

