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

// XDual Datarefs (prefix : xplmDr)
extern XPLMDataRef xplmDrStickPriority;
extern XPLMDataRef xplmDrRealStickPriority;
extern XPLMDataRef xplmDrRealRudderPriority;
extern XPLMDataRef xplmDrRealBrakesPriority;
extern XPLMDataRef xplmDrRealNWTillerPriority;

extern XPLMDataRef xplmDrCaptStickDeactivated;
extern XPLMDataRef xplmDrFoStickDeactivated;

extern XPLMDataRef xplmDrCaptStickFailed;
extern XPLMDataRef xplmDrFoStickFailed;
extern XPLMDataRef xplmDrCaptRudderFailed;
extern XPLMDataRef xplmDrFoRudderFailed;
extern XPLMDataRef xplmDrCaptBrakesFailed;
extern XPLMDataRef xplmDrFoBrakesFailed;
extern XPLMDataRef xplmDrCaptNWTillerFailed;
extern XPLMDataRef xplmDrFoNWTillerFailed;

extern XPLMDataRef xplmDrDualInput;

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

#endif /* DATAREFS_XDUAL_H_ */
