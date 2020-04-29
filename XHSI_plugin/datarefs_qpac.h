/*
 * datarefs_qpac.h
 *
 *  Created on: 19 févr. 2014
 *      Author: Nicolas Carel
 */

#ifndef DATAREFS_QPAC_H_
#define DATAREFS_QPAC_H_

// Key codes (values passed with QPAC_KEY_PRESS ID)
#define QPAC_KEY_TO_CONFIG 1
#define QPAC_KEY_PUSH_ALT  2
#define QPAC_KEY_PULL_ALT  3
#define QPAC_KEY_PUSH_VS   4
#define QPAC_KEY_PULL_VS   5
#define QPAC_KEY_PUSH_HDG  6
#define QPAC_KEY_PULL_HDG  7
#define QPAC_KEY_PUSH_SPD  8
#define QPAC_KEY_PULL_SPD  9
#define QPAC_KEY_ATHR      10
#define QPAC_KEY_APPR      11
#define QPAC_KEY_EXPED     12
#define QPAC_KEY_LOC       13
// Brakes
#define QPAC_KEY_ABRK_LOW  14
#define QPAC_KEY_ABRK_MED  15
#define QPAC_KEY_ABRK_MAX  16
// Pursers
#define QPAC_KEY_CHECK_CAB 17
// Radio panels
#define QPAC_KEY_RMP1_VHF1          18
#define QPAC_KEY_RMP1_VHF2          19
#define QPAC_KEY_RMP1_FREQ_DOWN_LRG 20
#define QPAC_KEY_RMP1_FREQ_DOWN_SML 21
#define QPAC_KEY_RMP1_FREQ_UP_LRG   22
#define QPAC_KEY_RMP1_FREQ_UP_SML   23
#define QPAC_KEY_RMP1_SWAP          24
#define QPAC_KEY_RMP2_VHF1          25
#define QPAC_KEY_RMP2_VHF2          26
#define QPAC_KEY_RMP2_FREQ_DOWN_LRG 27
#define QPAC_KEY_RMP2_FREQ_DOWN_SML 28
#define QPAC_KEY_RMP2_FREQ_UP_LRG   29
#define QPAC_KEY_RMP2_FREQ_UP_SML   30
#define QPAC_KEY_RMP2_SWAP          31
#define QPAC_KEY_RMP3_VHF1          32
#define QPAC_KEY_RMP3_VHF2          33
#define QPAC_KEY_RMP3_FREQ_DOWN_LRG 34
#define QPAC_KEY_RMP3_FREQ_DOWN_SML 35
#define QPAC_KEY_RMP3_FREQ_UP_LRG   36
#define QPAC_KEY_RMP3_FREQ_UP_SML   37
#define QPAC_KEY_RMP3_SWAP          38
#define QPAC_KEY_LISTEN_VHF1        39
#define QPAC_KEY_LISTEN_VHF2        40
// EFIS Chrono
#define QPAC_KEY_CHRONO_CAPT 41
#define QPAC_KEY_CHRONO_FO   42
// Fire test
#define QPAC_KEY_FIRE_TEST_ALL    43
#define QPAC_KEY_FIRE_TEST_APU    44
#define QPAC_KEY_FIRE_TEST_CARGO  45
#define QPAC_KEY_FIRE_TEST_ENG1   46
#define QPAC_KEY_FIRE_TEST_ENG2   47
#define QPAC_KEY_FIRE_TEST_ENG3   48
#define QPAC_KEY_FIRE_TEST_ENG4   49
// Transponder
#define QPAC_KEY_ATC_0    50
#define QPAC_KEY_ATC_1    51
#define QPAC_KEY_ATC_2    52
#define QPAC_KEY_ATC_3    53
#define QPAC_KEY_ATC_4    54
#define QPAC_KEY_ATC_5    55
#define QPAC_KEY_ATC_6    56
#define QPAC_KEY_ATC_7    57
#define QPAC_KEY_ATC_CLR  58

// GPWS commands
#define QPAC_KEY_GPWS_MUTE    59
#define QPAC_KEY_GPWS_SHUTOFF 60

// Cockpit Voice Recorder
#define QPAC_KEY_CVR_ERASE    61
#define QPAC_KEY_CVR_TEST     62

// Cabine Pressure
#define QPAC_KEY_CAB_VS_UP    63
#define QPAC_KEY_CAB_VS_DOWN  64

// Purser
#define QPAC_KEY_PURSER_AFT   65
#define QPAC_KEY_PURSER_ALL   66
#define QPAC_KEY_PURSER_EXIT  67
#define QPAC_KEY_PURSER_FWD   68
#define QPAC_KEY_PURSER_MECH  69
#define QPAC_KEY_PURSER_MID   70

// EVAC
#define QPAC_KEY_EVAC_HORN_SHUTOFF 71

// ECAM
#define QPAC_KEY_ECAM_ALL         72
#define QPAC_KEY_ECAM_RECALL      73
#define QPAC_KEY_ECAM_EMER_CANCEL 74

// CONFIG
#define QPAC_KEY_ADIRU_FAST_ALIGN 75
#define QPAC_KEY_SET_WEIGHT_CG    76

// MCDU1 KEYS
#define QPAC_KEY_MDCU1_INIT       80
#define QPAC_KEY_MDCU1_DATA       81
#define QPAC_KEY_MDCU1_MENU       82
#define QPAC_KEY_MDCU1_PERF       83
#define QPAC_KEY_MDCU1_PROG       84
#define QPAC_KEY_MDCU1_FPLN       85
#define QPAC_KEY_MDCU1_DIR_TO     86
#define QPAC_KEY_MDCU1_RAD_NAV    87
#define QPAC_KEY_MDCU1_AIRPORT    88
#define QPAC_KEY_MDCU1_SEC_FPLN   89
#define QPAC_KEY_MDCU1_FUEL_PRED  90
#define QPAC_KEY_MDCU1_ATC_COMM   91
#define QPAC_KEY_MDCU1_SLEW_UP    92
#define QPAC_KEY_MDCU1_SLEW_DOWN  93
#define QPAC_KEY_MDCU1_SLEW_LEFT  94
#define QPAC_KEY_MDCU1_SLEW_RIGHT 95
#define QPAC_KEY_MDCU1_LSK1L      96
#define QPAC_KEY_MDCU1_LSK2L      97
#define QPAC_KEY_MDCU1_LSK3L      98
#define QPAC_KEY_MDCU1_LSK4L      99
#define QPAC_KEY_MDCU1_LSK5L      100
#define QPAC_KEY_MDCU1_LSK6L      101
#define QPAC_KEY_MDCU1_LSK1R      102
#define QPAC_KEY_MDCU1_LSK2R      103
#define QPAC_KEY_MDCU1_LSK3R      104
#define QPAC_KEY_MDCU1_LSK4R      105
#define QPAC_KEY_MDCU1_LSK5R      106
#define QPAC_KEY_MDCU1_LSK6R      107
#define QPAC_KEY_MDCU1_DEL        108
#define QPAC_KEY_MDCU1_SPACE      109
#define QPAC_KEY_MDCU1_OVERFL     110
#define QPAC_KEY_MDCU1_PLUS_M     111
#define QPAC_KEY_MDCU1_DOT        112
#define QPAC_KEY_MDCU1_SLASH      113
#define QPAC_KEY_MDCU1_0          114
#define QPAC_KEY_MDCU1_1          115
#define QPAC_KEY_MDCU1_2          116
#define QPAC_KEY_MDCU1_3          117
#define QPAC_KEY_MDCU1_4          118
#define QPAC_KEY_MDCU1_5          119
#define QPAC_KEY_MDCU1_6          120
#define QPAC_KEY_MDCU1_7          121
#define QPAC_KEY_MDCU1_8          122
#define QPAC_KEY_MDCU1_9          123
#define QPAC_KEY_MDCU1_A          124
#define QPAC_KEY_MDCU1_B          125
#define QPAC_KEY_MDCU1_C          126
#define QPAC_KEY_MDCU1_D          127
#define QPAC_KEY_MDCU1_E          128
#define QPAC_KEY_MDCU1_F          129
#define QPAC_KEY_MDCU1_G          130
#define QPAC_KEY_MDCU1_H          131
#define QPAC_KEY_MDCU1_I          132
#define QPAC_KEY_MDCU1_J          133
#define QPAC_KEY_MDCU1_K          134
#define QPAC_KEY_MDCU1_L          135
#define QPAC_KEY_MDCU1_M          136
#define QPAC_KEY_MDCU1_N          137
#define QPAC_KEY_MDCU1_O          138
#define QPAC_KEY_MDCU1_P          139
#define QPAC_KEY_MDCU1_Q          140
#define QPAC_KEY_MDCU1_R          141
#define QPAC_KEY_MDCU1_S          142
#define QPAC_KEY_MDCU1_T          143
#define QPAC_KEY_MDCU1_U          144
#define QPAC_KEY_MDCU1_V          145
#define QPAC_KEY_MDCU1_W          146
#define QPAC_KEY_MDCU1_X          147
#define QPAC_KEY_MDCU1_Y          148
#define QPAC_KEY_MDCU1_Z          149

// MCDU2 KEYS
#define QPAC_KEY_MDCU2_INIT       150
#define QPAC_KEY_MDCU2_DATA       151
#define QPAC_KEY_MDCU2_MENU       152
#define QPAC_KEY_MDCU2_PERF       153
#define QPAC_KEY_MDCU2_PROG       154
#define QPAC_KEY_MDCU2_FPLN       155
#define QPAC_KEY_MDCU2_DIR_TO     156
#define QPAC_KEY_MDCU2_RAD_NAV    157
#define QPAC_KEY_MDCU2_AIRPORT    158
#define QPAC_KEY_MDCU2_SEC_FPLN   159
#define QPAC_KEY_MDCU2_FUEL_PRED  160
#define QPAC_KEY_MDCU2_ATC_COMM   161
#define QPAC_KEY_MDCU2_SLEW_UP    162
#define QPAC_KEY_MDCU2_SLEW_DOWN  163
#define QPAC_KEY_MDCU2_SLEW_LEFT  164
#define QPAC_KEY_MDCU2_SLEW_RIGHT 165
#define QPAC_KEY_MDCU2_LSK1L      166
#define QPAC_KEY_MDCU2_LSK2L      167
#define QPAC_KEY_MDCU2_LSK3L      168
#define QPAC_KEY_MDCU2_LSK4L      169
#define QPAC_KEY_MDCU2_LSK5L      170
#define QPAC_KEY_MDCU2_LSK6L      171
#define QPAC_KEY_MDCU2_LSK1R      172
#define QPAC_KEY_MDCU2_LSK2R      173
#define QPAC_KEY_MDCU2_LSK3R      174
#define QPAC_KEY_MDCU2_LSK4R      175
#define QPAC_KEY_MDCU2_LSK5R      176
#define QPAC_KEY_MDCU2_LSK6R      177
#define QPAC_KEY_MDCU2_DEL        178
#define QPAC_KEY_MDCU2_SPACE      179
#define QPAC_KEY_MDCU2_OVERFL     180
#define QPAC_KEY_MDCU2_PLUS_M     181
#define QPAC_KEY_MDCU2_DOT        182
#define QPAC_KEY_MDCU2_SLASH      183
#define QPAC_KEY_MDCU2_0          184
#define QPAC_KEY_MDCU2_1          185
#define QPAC_KEY_MDCU2_2          186
#define QPAC_KEY_MDCU2_3          187
#define QPAC_KEY_MDCU2_4          188
#define QPAC_KEY_MDCU2_5          189
#define QPAC_KEY_MDCU2_6          190
#define QPAC_KEY_MDCU2_7          191
#define QPAC_KEY_MDCU2_8          192
#define QPAC_KEY_MDCU2_9          193
#define QPAC_KEY_MDCU2_A          194
#define QPAC_KEY_MDCU2_B          195
#define QPAC_KEY_MDCU2_C          196
#define QPAC_KEY_MDCU2_D          197
#define QPAC_KEY_MDCU2_E          198
#define QPAC_KEY_MDCU2_F          199
#define QPAC_KEY_MDCU2_G          200
#define QPAC_KEY_MDCU2_H          201
#define QPAC_KEY_MDCU2_I          202
#define QPAC_KEY_MDCU2_J          203
#define QPAC_KEY_MDCU2_K          204
#define QPAC_KEY_MDCU2_L          205
#define QPAC_KEY_MDCU2_M          206
#define QPAC_KEY_MDCU2_N          207
#define QPAC_KEY_MDCU2_O          208
#define QPAC_KEY_MDCU2_P          209
#define QPAC_KEY_MDCU2_Q          210
#define QPAC_KEY_MDCU2_R          211
#define QPAC_KEY_MDCU2_S          212
#define QPAC_KEY_MDCU2_T          213
#define QPAC_KEY_MDCU2_U          214
#define QPAC_KEY_MDCU2_V          215
#define QPAC_KEY_MDCU2_W          216
#define QPAC_KEY_MDCU2_X          217
#define QPAC_KEY_MDCU2_Y          218
#define QPAC_KEY_MDCU2_Z          219

// Commands on airbus_qpac/.. dataref tree
// or on toliss_airbus/.. dataref tree
#define QPAC_KEY_AP1_PUSH         220
#define QPAC_KEY_AP2_PUSH         221
#define QPAC_KEY_FD1_PUSH         222
#define QPAC_KEY_FD2_PUSH         223
// END
#define QPAC_KEY_MAX              224

// global vars

extern XPLMDataRef qpac_plugin_status;

// Autopilot
extern XPLMDataRef qpac_ap1;
extern XPLMDataRef qpac_ap2;
extern XPLMDataRef qpac_ap_phase;
extern XPLMDataRef qpac_presel_crz;
extern XPLMDataRef qpac_presel_clb;
extern XPLMDataRef qpac_presel_mach;
extern XPLMDataRef qpac_ap_vertical_mode;
extern XPLMDataRef qpac_ap_vertical_armed;
extern XPLMDataRef qpac_ap_lateral_mode;
extern XPLMDataRef qpac_ap_lateral_armed;
extern XPLMDataRef qpac_npa_valid;
extern XPLMDataRef qpac_npa_no_points;
extern XPLMDataRef qpac_npa_slope;
extern XPLMDataRef qpac_appr_illuminated;
extern XPLMDataRef qpac_loc_illuminated;
extern XPLMDataRef qpac_appr_type;
extern XPLMDataRef qpac_appr_mda;
extern XPLMDataRef qpac_alt_is_cstr;
extern XPLMDataRef qpac_constraint_alt;
// FCU
extern XPLMDataRef qpac_fcu_hdg_trk;
extern XPLMDataRef qpac_fcu_metric_alt;
extern XPLMDataRef qpac_fcu_vs_dashed;
extern XPLMDataRef qpac_fcu_spd_dashed;
extern XPLMDataRef qpac_fcu_spd_managed;
extern XPLMDataRef qpac_fcu_hdg_dashed;
extern XPLMDataRef qpac_fcu_hdg_managed;
extern XPLMDataRef qpac_fcu_alt_managed;
// Auto-Thrust and speed
extern XPLMDataRef qpac_athr_mode;
extern XPLMDataRef qpac_athr_mode2;
extern XPLMDataRef qpac_athr_limited;
extern XPLMDataRef qpac_thr_lever_mode;
extern XPLMDataRef qpac_fma_thr_warning;
extern XPLMDataRef qpac_flex_temp;
extern XPLMDataRef qpac_thr_rating_type;
extern XPLMDataRef qpac_thr_rating_n1;
extern XPLMDataRef qpac_thr_rating_epr;
extern XPLMDataRef qpac_throttle_input;
extern XPLMDataRef qpac_eng_epr_array;
// ILS Sig and Deviation Capt. and FO
extern XPLMDataRef qpac_loc_val_capt;
extern XPLMDataRef qpac_loc_on_capt;
extern XPLMDataRef qpac_gs_val_capt;
extern XPLMDataRef qpac_gs_on_capt;
extern XPLMDataRef qpac_ils_on_capt;
extern XPLMDataRef qpac_loc_val_fo;
extern XPLMDataRef qpac_loc_on_fo;
extern XPLMDataRef qpac_gs_val_fo;
extern XPLMDataRef qpac_gs_on_fo;
extern XPLMDataRef qpac_ils_on_fo;
extern XPLMDataRef qpac_ils_crs;
extern XPLMDataRef qpac_ils_1;
extern XPLMDataRef qpac_ils_2;
extern XPLMDataRef qpac_ils_3;
// FD
extern XPLMDataRef qpac_fd1;
extern XPLMDataRef qpac_fd2;
extern XPLMDataRef qpac_fd1_ver_bar;
extern XPLMDataRef qpac_fd1_hor_bar;
extern XPLMDataRef qpac_fd1_yaw_bar;
extern XPLMDataRef qpac_fd2_ver_bar;
extern XPLMDataRef qpac_fd2_hor_bar;
extern XPLMDataRef qpac_fd2_yaw_bar;
// Baro
extern XPLMDataRef qpac_baro_std_capt;
extern XPLMDataRef qpac_baro_unit_capt;
extern XPLMDataRef qpac_baro_hide_capt;
extern XPLMDataRef qpac_baro_std_fo;
extern XPLMDataRef qpac_baro_unit_fo;
extern XPLMDataRef qpac_baro_hide_fo;
// Compass
extern XPLMDataRef qpac_true_mag;
// V Speeds
extern XPLMDataRef qpac_v1_value;
extern XPLMDataRef qpac_v1;
extern XPLMDataRef qpac_vr;
extern XPLMDataRef qpac_vmo;
extern XPLMDataRef qpac_vls;
extern XPLMDataRef qpac_vf;
extern XPLMDataRef qpac_vs;
extern XPLMDataRef qpac_v_green_dot;
extern XPLMDataRef qpac_alpha_prot;
extern XPLMDataRef qpac_alpha_max;
extern XPLMDataRef qpac_vfe_next;
extern XPLMDataRef qpac_v_green_dot_value;
extern XPLMDataRef qpac_vf_value;
extern XPLMDataRef qpac_vs_value;
extern XPLMDataRef qpac_v2_value;
extern XPLMDataRef qpac_vr_value;
extern XPLMDataRef qpac_alpha_floor_mode;

// Failures
extern XPLMDataRef qpac_capt_hdg_valid;
extern XPLMDataRef qpac_capt_att_valid;
extern XPLMDataRef qpac_capt_ias_valid;
extern XPLMDataRef qpac_capt_alt_valid;
extern XPLMDataRef qpac_co_hdg_valid;
extern XPLMDataRef qpac_co_att_valid;
extern XPLMDataRef qpac_co_ias_valid;
extern XPLMDataRef qpac_co_alt_valid;
// EFIS
extern XPLMDataRef qpac_capt_efis_nd_mode;
extern XPLMDataRef qpac_co_efis_nd_mode;
extern XPLMDataRef qpac_capt_efis_nd_range;
extern XPLMDataRef qpac_co_efis_nd_range;
// GPWS
extern XPLMDataRef qpac_gpws_switch_array;
// Brakes
// 0=OFF, 1=Engaged, 2=DECEL
extern XPLMDataRef qpac_autobrake_low;
extern XPLMDataRef qpac_autobrake_med;
extern XPLMDataRef qpac_autobrake_max;
extern XPLMDataRef qpac_left_brake_release;
extern XPLMDataRef qpac_right_brake_release;
extern XPLMDataRef qpac_nw_anti_skid;
// Version 2.0.4
extern XPLMDataRef qpac_brake_fan;
extern XPLMDataRef qpac_brake_temp_array;
extern XPLMDataRef qpac_tire_press_array;
// Gears indicators
extern XPLMDataRef qpac_left_gear_ind;
extern XPLMDataRef qpac_nose_gear_ind;
extern XPLMDataRef qpac_right_gear_ind;
// Triple brake indicator
extern XPLMDataRef qpac_brake_accu;
extern XPLMDataRef qpac_tot_right_brake;
extern XPLMDataRef qpac_tot_left_brake;
// Flaps and slats
extern XPLMDataRef qpac_flaps_request_pos;
extern XPLMDataRef qpac_slats_request_pos;
// Left and right ailerons
extern XPLMDataRef qpac_right_aileron_pos;
extern XPLMDataRef qpac_left_aileron_pos;
// Spoilers
extern XPLMDataRef qpac_spoilers_array;
// ELAC and SEC
extern XPLMDataRef qpac_fcc_avail_array;
// Rudder limits
extern XPLMDataRef qpac_rudder_limit_pos;
// Hydraulics
extern XPLMDataRef qpac_hyd_pressure_array;
extern XPLMDataRef qpac_hyd_pump_array;
extern XPLMDataRef qpac_hyd_rat_mode;
extern XPLMDataRef qpac_hyd_y_elec_mode;
extern XPLMDataRef qpac_hyd_ptu_mode;
extern XPLMDataRef qpac_hyd_sys_qty_array;
// fire valve : sim/cockpit2/engine/fire_estinguisher_on[0,1] boolean
// Cabin Pressure
extern XPLMDataRef qpac_cabin_delta_p;
extern XPLMDataRef qpac_cabin_alt;
extern XPLMDataRef qpac_cabin_vs;
extern XPLMDataRef qpac_outflow_valve;
extern XPLMDataRef qpac_cabin_man_press_mode;
// ENG lower ECAM (ignition and nacelles)
extern XPLMDataRef qpac_eng_mode_switch;
extern XPLMDataRef qpac_ewd_start_mode;
extern XPLMDataRef qpac_start_valve_array;
extern XPLMDataRef qpac_nacelle_temp_array;
// COND
extern XPLMDataRef qpac_cond_hot_air_valve;
extern XPLMDataRef qpac_cond_cockpit_trim;
extern XPLMDataRef qpac_cond_zone1_trim;
extern XPLMDataRef qpac_cond_zone2_trim;
extern XPLMDataRef qpac_cond_cockpit_temp;
extern XPLMDataRef qpac_cond_aft_cabin_temp;
extern XPLMDataRef qpac_cond_fwd_cabin_temp;
// version 2.0.4
extern XPLMDataRef qpac_cond_cargo_hot_air_valve;
extern XPLMDataRef qpac_cond_aft_cargo_temp;
extern XPLMDataRef qpac_cond_fwd_cargo_temp;
extern XPLMDataRef qpac_cond_aft_cargo_isol_valve;
extern XPLMDataRef qpac_cond_fwd_cargo_isol_valve;
// Doors - Oxygen
extern XPLMDataRef qpac_door_pax_array;
extern XPLMDataRef qpac_door_cargo_array;
extern XPLMDataRef qpac_door_bulk_door;
extern XPLMDataRef qpac_crew_oxy_mask;
// Bleed
extern XPLMDataRef qpac_bleed_intercon;
extern XPLMDataRef qpac_bleed_x;
extern XPLMDataRef qpac_bleed_apu;
extern XPLMDataRef qpac_bleed_eng1;
extern XPLMDataRef qpac_bleed_eng2;
extern XPLMDataRef qpac_bleed_eng1_hp;
extern XPLMDataRef qpac_bleed_eng2_hp;
extern XPLMDataRef qpac_bleed_pack1_fcv;
extern XPLMDataRef qpac_bleed_pack2_fcv;
extern XPLMDataRef qpac_bleed_pack1_flow;
extern XPLMDataRef qpac_bleed_pack2_flow;
extern XPLMDataRef qpac_bleed_pack1_temp;
extern XPLMDataRef qpac_bleed_pack2_temp;
extern XPLMDataRef qpac_bleed_ram_air;
extern XPLMDataRef qpac_bleed_ram_air_valve;
extern XPLMDataRef qpac_bleed_left_press;
extern XPLMDataRef qpac_bleed_right_press;
// APU
extern XPLMDataRef qpac_apu_egt;
extern XPLMDataRef qpac_apu_egt_limit;
// ELEC
extern XPLMDataRef qpac_elec_ext_pow_box;
extern XPLMDataRef qpac_elec_ac_cross_connect;
extern XPLMDataRef qpac_elec_connect_left;
extern XPLMDataRef qpac_elec_connect_center;
extern XPLMDataRef qpac_elec_connect_right;
extern XPLMDataRef qpac_elec_battery_supply;
extern XPLMDataRef qpac_elec_connectors;
extern XPLMDataRef qpac_elec_ohp_array;
extern XPLMDataRef qpac_elec_apu_box;
extern XPLMDataRef qpac_elec_battery_volt;
// FUEL
extern XPLMDataRef qpac_fuel_pump_array;
extern XPLMDataRef qpac_fuel_xfv_array ;
extern XPLMDataRef qpac_fuel_eng_lp_valve_array;
extern XPLMDataRef qpac_fuel_tv_array;
extern XPLMDataRef qpac_fuel_auto_pump_ohp_array;
extern XPLMDataRef qpac_fuel_auto_pump_sd_array;
extern XPLMDataRef qpac_fuel_pump_ohp_array;
extern XPLMDataRef qpac_fuel_pump_sd_array;
extern XPLMDataRef qpac_fuel_ohp_auto_switch;
// ECAM SD page selection
extern XPLMDataRef qpac_sd_page;
extern XPLMDataRef qpac_clear_illuminated;
extern XPLMDataRef qpac_sd_eng;
extern XPLMDataRef qpac_sd_bleed;
extern XPLMDataRef qpac_sd_press;
extern XPLMDataRef qpac_sd_elec;
extern XPLMDataRef qpac_sd_hyd;
extern XPLMDataRef qpac_sd_fuel;
extern XPLMDataRef qpac_sd_apu;
extern XPLMDataRef qpac_sd_cond;
extern XPLMDataRef qpac_sd_door;
extern XPLMDataRef qpac_sd_wheel;
extern XPLMDataRef qpac_sd_fctl;
extern XPLMDataRef qpac_sd_status;

// ECAM SD lines (a,b,g,r,w)
#define QPAC_SD_LINES 18
extern XPLMDataRef qpac_sd_line_amber[QPAC_SD_LINES];
extern XPLMDataRef qpac_sd_line_blue[QPAC_SD_LINES];
extern XPLMDataRef qpac_sd_line_green[QPAC_SD_LINES];
extern XPLMDataRef qpac_sd_line_red[QPAC_SD_LINES];
extern XPLMDataRef qpac_sd_line_white[QPAC_SD_LINES];
// ECAM FWC Flight Warning Computer
XPLMDataRef qpac_ecam_flight_phase;

// Anti-ice
extern XPLMDataRef qpac_wing_anti_ice;
extern XPLMDataRef qpac_wing_anti_ice_lights;
extern XPLMDataRef qpac_eng1_anti_ice;
extern XPLMDataRef qpac_eng1_anti_ice_lights;
extern XPLMDataRef qpac_eng2_anti_ice;
extern XPLMDataRef qpac_eng2_anti_ice_lights;

// Chrono
extern XPLMDataRef qpac_chrono_nd1;
extern XPLMDataRef qpac_chrono_nd2;

// Terrain
extern XPLMDataRef qpac_terrain_nd1;
extern XPLMDataRef qpac_terrain_nd2;

// Weather radar
extern XPLMDataRef qpac_wx_power_switch;

// Navigation display - TO Waypoint
extern XPLMDataRef qpac_wpt_crs_capt;
extern XPLMDataRef qpac_wpt_crs_fo;
extern XPLMDataRef qpac_wpt_dist_capt;
extern XPLMDataRef qpac_wpt_dist_fo;
extern XPLMDataRef qpac_wpt_hours_capt;
extern XPLMDataRef qpac_wpt_hours_fo;
extern XPLMDataRef qpac_wpt_minutes_capt;
extern XPLMDataRef qpac_wpt_minutes_fo;
extern XPLMDataRef qpac_wpt_id_capt;
extern XPLMDataRef qpac_wpt_id_fo;

//qpac FCU toggles, push/pull commands, RMP, MCDU
extern XPLMCommandRef qpac_command[QPAC_KEY_MAX];

extern int qpac_ready;

/* QPAC Versions :
 *  major version number is x100
 *  0 = Not ready
 *  110 = QPAC Freeware 1.1
 *  150 = PeterAircraft A320
 *  202 = QPAC 2.02 Final Basic
 */
#define QPAC_VERSION_1_1 110
#define QPAC_VERSION_PA  150
#define QPAC_VERSION_2_02_BASIC 202
extern int qpac_version;

// global functions
float checkQpacCallback(float, float, int, void *);
void writeQpacDataRef(int, float);

#endif /* DATAREFS_QPAC_H_ */
