/*
 * datarefs_q400.h
 *
 *  Created on: 8 janv. 2022
 *      Author: -
 */

#ifndef DATAREFS_Q400_H_
#define DATAREFS_Q400_H_


// global vars

// extern XPLMDataRef x737_plugin_status;
// in Arbeit, noch nicht eingefügt
// x737/systems/afds/...
extern XPLMDataRef q400_plugin_status;

extern XPLMDataRef FJS_PDF1_rmi_Circle;
extern XPLMDataRef FJS_PDF1_rmi_Diamond;
extern XPLMDataRef sim_cockpit_radios_obs_mag;
extern XPLMDataRef FJS_FADEC_rating;
extern XPLMDataRef FJS_feath_AFarm;
extern XPLMDataRef FJS_bleed_bleedAnun;
extern XPLMDataRef FJS_FADEC_powerPercent;
extern XPLMDataRef FJS_PDF1_airspeed_airBugV1;
extern XPLMDataRef FJS_PDF1_airspeed_airBugV2;
extern XPLMDataRef FJS_PDF1_airspeed_airBugVr;
extern XPLMDataRef FJS_PDF1_airspeed_airBugVclimb;
extern XPLMDataRef FJS_PDF1_airspeed_airBugVref;

extern int q400_ready;

// global functions
void findQ400DataRefs(void);
float checkQ400Callback(float, float, int, void *);

#endif /* DATAREFS_Q400_H_ */
