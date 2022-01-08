/**
* datarefs.h
*
* This X-Plane plugin manage dual yoke and rudder controls.
*
* Created on 19 déc. 2021 by Nicolas Carel
* Copyright (C) 2020  Nicolas Carel
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

#ifndef DATAREFS_XDUAL_H_
#define DATAREFS_XDUAL_H_

#define XDUAL_PLUGIN_SIGNATURE "xdual.plugin"

// XDual commands
#define XDUAL_KEY_STICK_CAPT 70
#define XDUAL_KEY_STICK_FO 71
// This buttons are on each side stick
// A first press will disconnect the AP
// A second press will take priority
// A third press will restore dual controls
#define XDUAL_PRIORITY_PB_CAPT 72
#define XDUAL_PRIORITY_PB_FO 73
#define XDUAL_PRIORITY_PB_CAPT_PRESS 74
#define XDUAL_PRIORITY_PB_FO_PRESS 75
#define XDUAL_PRIORITY_PB_CAPT_RELEASE 76
#define XDUAL_PRIORITY_PB_FO_RELEASE 77


// XDual Datarefs (prefix : xplmDr)
extern XPLMDataRef xplmDrStickPriority;
extern XPLMDataRef xplmDrRealStickPriority;
extern XPLMDataRef xplmDrRealRudderPriority;
extern XPLMDataRef xplmDrRealBrakesPriority;
extern XPLMDataRef xplmDrRealNWTillerPriority;

extern XPLMDataRef xplmDrCaptStickDeactivated;
extern XPLMDataRef xplmDrFoStickDeactivated;

// Failures status
extern XPLMDataRef xplmDrCaptStickFailed;
extern XPLMDataRef xplmDrFoStickFailed;
extern XPLMDataRef xplmDrCaptRudderFailed;
extern XPLMDataRef xplmDrFoRudderFailed;
extern XPLMDataRef xplmDrCaptBrakesFailed;
extern XPLMDataRef xplmDrFoBrakesFailed;
extern XPLMDataRef xplmDrCaptNWTillerFailed;
extern XPLMDataRef xplmDrFoNWTillerFailed;

// Dual input status
extern XPLMDataRef xplmDrDualInput;

// Messages
extern XPLMDataRef xplmDrMsgDualInput;
extern XPLMDataRef xplmDrMsgPriorityLeft;
extern XPLMDataRef xplmDrMsgPriorityRight;

// Indicators
extern XPLMDataRef xplmDrIndicatorCaptPriority;
extern XPLMDataRef xplmDrIndicatorFoPriority;
extern XPLMDataRef xplmDrIndicatorCaptArrow;
extern XPLMDataRef xplmDrIndicatorFoArrow;

// XDual commands
extern XPLMCommandRef stick_takeover_capt;
extern XPLMCommandRef stick_takeover_fo;
extern XPLMCommandRef stick_priority_capt;
extern XPLMCommandRef stick_priority_fo;

extern int xdual_ready;

// global functions
float checkXDualCallback(float, float, int, void *);
void writeXDualDataRef(int, float);

#endif /* DATAREFS_XDUAL_H_ */
