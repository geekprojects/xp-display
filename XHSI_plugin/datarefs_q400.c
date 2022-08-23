/*
 * datarefs_q400.c
 *
 *  Created on: 8 janv. 2022
 *      Author: -
 */



#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <stdint.h>


#define XPLM200

//#include "XPLMProcessing.h"
#include "XPLMDataAccess.h"
#include "XPLMUtilities.h"
//#include "XPLMNavigation.h"
//#include "XPLMDisplay.h"
//#include "XPLMMenus.h"
//#include "XPWidgets.h"
//#include "XPStandardWidgets.h"



// DataRefs for the q400

XPLMDataRef q400_plugin_status;

XPLMDataRef FJS_PDF1_rmi_Circle;
XPLMDataRef FJS_PDF1_rmi_Diamond;
XPLMDataRef sim_cockpit_radios_obs_mag;
XPLMDataRef FJS_FADEC_rating;
XPLMDataRef FJS_feath_AFarm;
XPLMDataRef FJS_bleed_bleedAnun;
XPLMDataRef FJS_FADEC_powerPercent;

XPLMDataRef FJS_PDF1_airspeed_airBugV1;
XPLMDataRef FJS_PDF1_airspeed_airBugV2;
XPLMDataRef FJS_PDF1_airspeed_airBugVr;
XPLMDataRef FJS_PDF1_airspeed_airBugVclimb;
XPLMDataRef FJS_PDF1_airspeed_airBugVref;



int q400_ready = 0;


void findQ400DataRefs(void) {

	q400_plugin_status = XPLMFindDataRef("FJS/727/Elec/pow");

	// q400_plugin_status = 1;
	// if (q400_ready != 1) q400_ready = 0;

	if ( q400_plugin_status == NULL ) {
		// XPLMDebugString("XHSI: not using Q400");
		if ( q400_ready == 1 ) XPLMDebugString("XHSI: Bombardier Q400 vanished\n");
        q400_ready = 0;

    } else {

        if ( q400_ready == 0 ) {

            q400_ready = 1;

            XPLMDebugString("XHSI: using Bombardier Q400 DataRefs\n");

			FJS_PDF1_rmi_Circle = XPLMFindDataRef("FJS/PDF1/rmi/Circle");
			// FJS_PDF1_rmi_Circle = XPLMFindDataRef("Cdref_rmiCircle");
			FJS_PDF1_rmi_Diamond = XPLMFindDataRef("FJS/PDF1/rmi/Diamond");
			sim_cockpit_radios_obs_mag = XPLMFindDataRef("sim/cockpit/radios/obs_mag");
			FJS_FADEC_rating = XPLMFindDataRef("FJS/FADEC/rating");
			FJS_feath_AFarm = XPLMFindDataRef("FJS/feath/AFarm");
			FJS_bleed_bleedAnun = XPLMFindDataRef("FJS/bleed/bleedAnun");
			FJS_FADEC_powerPercent = XPLMFindDataRef("FJS/FADEC/powerPercent");
			FJS_PDF1_airspeed_airBugV1 = XPLMFindDataRef("FJS/PDF1/airspeed/airBugV1_val");
			FJS_PDF1_airspeed_airBugV2 = XPLMFindDataRef("FJS/PDF1/airspeed/airBugV2_val");
			FJS_PDF1_airspeed_airBugVr = XPLMFindDataRef("FJS/PDF1/airspeed/airBugVr_val");
			FJS_PDF1_airspeed_airBugVclimb = XPLMFindDataRef("FJS/PDF1/airspeed/airBugVclimb_val");
			FJS_PDF1_airspeed_airBugVref = XPLMFindDataRef("FJS/PDF1/airspeed/airBugVref_val");

        }

    }

}


float checkQ400Callback(
        float	inElapsedSinceLastCall,
        float	inElapsedTimeSinceLastFlightLoop,
        int		inCounter,
        void *	inRefcon) {

    findQ400DataRefs();

    // come back in 5sec
    return 5.0;

}
