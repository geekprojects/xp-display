/**
* datarefs_xdual.c
*
* XDual X-Plane plugin manages dual yoke and rudder controls.
* Provides A320 side stick priority indicators
*
* Created on: 22 sept. 2016 by Nicolas Carel
* Renamed from datarefs_xjoymap.c to  datarefs_xdual.c on: 1 jan. 2022
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

#include "XPLMPlugin.h"
#include "XPLMDataAccess.h"
#include "XPLMUtilities.h"

#include "ids.h"
#include "datarefs_xdual.h"

// Plugin ID
XPLMPluginID xdual_PluginId = XPLM_NO_PLUGIN_ID;

// XDual Datarefs (prefix : xplmDr)
XPLMDataRef xplmDrStickPriority = NULL;
XPLMDataRef xplmDrRealStickPriority = NULL;
XPLMDataRef xplmDrRealRudderPriority = NULL;
XPLMDataRef xplmDrRealBrakesPriority = NULL;
XPLMDataRef xplmDrRealNWTillerPriority = NULL;
XPLMDataRef xplmDrCaptStickDeactivated = NULL;
XPLMDataRef xplmDrFoStickDeactivated = NULL;
XPLMDataRef xplmDrCaptStickFailed = NULL;
XPLMDataRef xplmDrFoStickFailed = NULL;
XPLMDataRef xplmDrCaptRudderFailed = NULL;
XPLMDataRef xplmDrFoRudderFailed = NULL;
XPLMDataRef xplmDrCaptBrakesFailed = NULL;
XPLMDataRef xplmDrFoBrakesFailed = NULL;
XPLMDataRef xplmDrCaptNWTillerFailed = NULL;
XPLMDataRef xplmDrFoNWTillerFailed = NULL;
XPLMDataRef xplmDrDualInput = NULL;
XPLMDataRef xplmDrIndicatorCaptPriority = NULL;
XPLMDataRef xplmDrIndicatorFoPriority = NULL;
XPLMDataRef xplmDrIndicatorCaptArrow = NULL;
XPLMDataRef xplmDrIndicatorFoArrow = NULL;

int xdual_ready=0;

// XDual commands
XPLMCommandRef stick_takeover_capt = NULL;
XPLMCommandRef stick_takeover_fo = NULL;
XPLMCommandRef stick_priority_capt = NULL;
XPLMCommandRef stick_priority_fo = NULL;

void findXDualDataRefs(void) {

	// Check XDual plugin signature
	xdual_PluginId = XPLMFindPluginBySignature(XDUAL_PLUGIN_SIGNATURE);

	if ( xdual_PluginId == XPLM_NO_PLUGIN_ID ) {

		xdual_ready = 0;

    } else {
        if ( xdual_ready == 0 ) {

        	xdual_ready = 1;

            XPLMDebugString("XHSI: using XDual DataRefs and Commands\n");

            xplmDrStickPriority = XPLMFindDataRef("xdual/StickPriority");

            xplmDrRealStickPriority = XPLMFindDataRef("xdual/RealStickPriority");
            xplmDrRealRudderPriority = XPLMFindDataRef("xdual/RealRudderPriority");
        	xplmDrRealBrakesPriority = XPLMFindDataRef("xdual/RealBrakesPriority");
            xplmDrRealNWTillerPriority = XPLMFindDataRef("xdual/RealNWTillerPriority");

            xplmDrCaptStickDeactivated = XPLMFindDataRef("xdual/CaptStickDeactivated");
            xplmDrFoStickDeactivated = XPLMFindDataRef("xdual/FoStickDeactivated");

            xplmDrCaptStickFailed = XPLMFindDataRef("xdual/CaptStickFailed");
            xplmDrFoStickFailed = XPLMFindDataRef("xdual/FoStickFailed");
            xplmDrCaptRudderFailed = XPLMFindDataRef("xdual/CaptRudderFailed");
            xplmDrFoRudderFailed = XPLMFindDataRef("xdual/FoRudderFailed");
            xplmDrCaptBrakesFailed = XPLMFindDataRef("xdual/CaptBrakesFailed");
            xplmDrFoBrakesFailed = XPLMFindDataRef("xdual/FoBrakesFailed");
            xplmDrCaptNWTillerFailed = XPLMFindDataRef("xdual/CaptNWTillerFailed");
            xplmDrFoNWTillerFailed = XPLMFindDataRef("xdual/FoNWTillerFailed");

            xplmDrDualInput = XPLMFindDataRef("xdual/DualInput");

            xplmDrIndicatorCaptPriority = XPLMFindDataRef("xdual/indicators/CaptPriority");
            xplmDrIndicatorFoPriority = XPLMFindDataRef("xdual/indicators/FoPriority");
            xplmDrIndicatorCaptArrow = XPLMFindDataRef("xdual/indicators/CaptArrow");
            xplmDrIndicatorFoArrow = XPLMFindDataRef("xdual/indicators/FoArrow");

            // Commands
            stick_takeover_capt = XPLMFindCommand("xdual/StickTakeOverPBCapt");
            stick_takeover_fo = XPLMFindCommand("xdual/StickTakeOverPBFO");
            stick_priority_capt = XPLMFindCommand("xdual/StickPriorityCapt");
            stick_priority_fo = XPLMFindCommand("xdual/StickPriorityCapt");
        }
    }
}


float checkXDualCallback(
        float	inElapsedSinceLastCall,
        float	inElapsedTimeSinceLastFlightLoop,
        int		inCounter,
        void *	inRefcon) {

    findXDualDataRefs();

    // come back in 5 sec
    return 5.0;
}


/**
 * if (xdual_ready)
 */
void writeXDualDataRef(int id, float value) {
    char info_string[80];
    switch (id) {
		case XDUAL_INDICATORS :
			// No operations
			break;
		case XDUAL_KEY_PRESS :
			switch ((int)value) {

				case XDUAL_KEY_STICK_CAPT :
					XPLMCommandOnce(stick_priority_capt);
					break;
				case XDUAL_KEY_STICK_FO :
					XPLMCommandOnce(stick_priority_capt);
					break;
				case XDUAL_PRIORITY_PB_CAPT :
					XPLMCommandOnce(stick_priority_capt);
					break;
				case XDUAL_PRIORITY_PB_FO :
					XPLMCommandOnce(stick_priority_capt);
					break;
				case XDUAL_PRIORITY_PB_CAPT_PRESS :
					XPLMCommandBegin(stick_priority_capt);
					break;
				case XDUAL_PRIORITY_PB_FO_PRESS :
					XPLMCommandBegin(stick_priority_capt);
					break;
				case XDUAL_PRIORITY_PB_CAPT_RELEASE :
					XPLMCommandEnd(stick_priority_capt);
					break;
				case XDUAL_PRIORITY_PB_FO_RELEASE :
					XPLMCommandEnd(stick_priority_capt);
					break;

			}
			break;


    }
}
