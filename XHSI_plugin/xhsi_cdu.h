/*
 * xphsi_cdu.h
 *
 *  Created on: 28 sept 2021
 *      Author: Nicolas Carel
 */

#ifndef XHSI_CDU_H_
#define XHSI_CDU_H_

// XP11_CDU_BUF_LEN = QPAC_CDU_BUF_LEN = 80
#define XHSI_CDU_BUF_LEN 80
#define XHSI_CDU_MAX_MSG_COUNT 7
#define XHSI_CDU_LINES 16

// CDU TEXT LINE is UTF-8 encoded, may be up to 48 bytes
#define XHSI_CDU_TEXT_LINE_WIDTH 48
#define XHSI_CDU_LINE_WIDTH 24

extern int xhsi_cdu_msg_count;
extern int xhsi_fms_keypressed;

struct xhsiCduDisplayLine {
	int lineno;
	int len;
	char linestr[XHSI_CDU_BUF_LEN];
};

/**
 * The XHSI CDU Msg data packet format contains:
 * packet_id : QPAM (Compatible with Qpac MCDU data packet
 * nb_of_lines : 0 to 16 lines (QPAC MCDU is limit to 14 lines)
 * side : 0=left MCDU 1=right MCDU 2=observer MCDU
 * status : bit field
 *       - MCDU_FLAG_EXEC
 *       - MCDU_FLAG_MSG
 */
struct xhsiCduMsgLinesDataPacket {
    char packet_id[4];
	int nb_of_lines;
	int side;
	int status;
	struct xhsiCduDisplayLine lines[XHSI_CDU_LINES];
};

float sendXHSICduMsgCallback(float, float, int, void *);

#endif /* XHSI_CDU_H_ */
