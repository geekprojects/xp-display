

#ifndef DATAREFS_PILOTEDGE_H_
#define DATAREFS_PILOTEDGE_H_

// global vars

extern XPLMDataRef pilotedge_rx_status;
extern XPLMDataRef pilotedge_tx_status;
extern XPLMDataRef pilotedge_connected;

extern int pilotedge_ready;


// global functions
void findPilotEdgeDataRefs(void);
float checkPilotEdgeCallback(float, float, int, void *);

#endif /* DATAREFS_PILOTEDGE_H_ */
