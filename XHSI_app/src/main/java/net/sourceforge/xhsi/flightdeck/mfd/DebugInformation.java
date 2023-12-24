/**
* LowerEicas.java
* 
* Lower EICAS and ECAM - Debug and Information page
* 
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

package net.sourceforge.xhsi.flightdeck.mfd;


import java.awt.Component;
import java.awt.Graphics2D;

import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.QpacMcduData;
import net.sourceforge.xhsi.model.SimDataRepository;
import net.sourceforge.xhsi.model.xplane.XPlaneSimDataRepository;

public class DebugInformation extends MFDSubcomponent {

	private static final long serialVersionUID = 1L;

    // private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");
  
    private SimDataRepository sim_data;
    
  
	public DebugInformation(ModelFactory model_factory, MFDGraphicsConfig hsi_gc, Component parent_component) {
		super(model_factory, hsi_gc, parent_component);
		sim_data=model_factory.get_repository_instance();
	}

	public void paint(Graphics2D g2) {

		if ( avionics.get_mfd_mode() == Avionics.MFD_MODE_DEBUG_INFORMATION) {
			// Page ID
			drawPageID(g2, "XHSI DBG INFO");
			drawSeparation(g2);
			drawDebugInformations(g2);
		}
	}


	private void drawPageID(Graphics2D g2, String page_str) {
		g2.setColor(mfd_gc.ecam_markings_color);
		g2.setFont(mfd_gc.font_xxl);
		int page_id_x = mfd_gc.panel_rect.x + mfd_gc.digit_width_xxl;
		int page_id_y = mfd_gc.panel_rect.y + mfd_gc.line_height_xl * 11/10;     	
		g2.drawString(page_str, page_id_x, page_id_y);
		g2.drawLine(page_id_x, page_id_y + mfd_gc.line_height_xxl/8, page_id_x + mfd_gc.get_text_width(g2, mfd_gc.font_xxl, page_str), page_id_y + mfd_gc.line_height_xl/8);
	}

	private void drawSeparation(Graphics2D g2) {
		g2.setColor(mfd_gc.ecam_markings_color);
		g2.drawLine(mfd_gc.sys_line_x, mfd_gc.sys_line_top_y, mfd_gc.sys_line_x, mfd_gc.sys_line_bottom_y);
		
	}
	
	private void drawInformationsLine(Graphics2D g2, int line, String label, String value) {
		g2.setFont(mfd_gc.font_s);
        g2.setColor(mfd_gc.ecam_action_color);
        g2.drawString(label, mfd_gc.sys_info_x, mfd_gc.sys_info_top_y + line * mfd_gc.line_height_l);
        g2.setColor(mfd_gc.ecam_caution_color);
        g2.drawString(value, mfd_gc.sys_inop_x, mfd_gc.sys_inop_top_y + line * mfd_gc.line_height_l);
	}
	
	private void drawDebugInformations(Graphics2D g2) {
		String str_legend = "DATAREF / FUNCTION";
		String str_value = "VALUE";
		g2.setColor(mfd_gc.ecam_caution_color);
		g2.setFont(mfd_gc.font_xl);
		int page_id_x = mfd_gc.panel_rect.x + mfd_gc.panel_rect.width*3/4 - mfd_gc.get_text_width(g2, mfd_gc.font_xl, str_value)/2;
		int page_id_y = mfd_gc.panel_rect.y + mfd_gc.line_height_xl * 26/10;     	
		g2.drawString(str_value, page_id_x, page_id_y);
		g2.drawLine(page_id_x, page_id_y + mfd_gc.line_height_xl/8, page_id_x + mfd_gc.get_text_width(g2, mfd_gc.font_xl, str_value), page_id_y + mfd_gc.line_height_m/8);
		
		g2.setColor(mfd_gc.ecam_action_color);
		g2.drawString(str_legend, mfd_gc.sys_info_x, page_id_y);
		g2.drawLine(mfd_gc.sys_info_x, page_id_y + mfd_gc.line_height_xl/8, mfd_gc.sys_info_x + mfd_gc.get_text_width(g2, mfd_gc.font_xl, str_legend), page_id_y + mfd_gc.line_height_m/8);
		
        drawInformationsLine(g2, 1, "sim/aircraft/view/acf_ICAO", aircraft.aircraft_icao_type());
        drawInformationsLine(g2, 2, "sim/aircraft/view/acf_tailnum", aircraft.aircraft_registration());
        drawInformationsLine(g2, 3, "QPAC status", ""+sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_STATUS));
        int door_status = (int) sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_DOOR_STATUS);
        drawInformationsLine(g2, 4, "QPAC door status", "(bin) "+Integer.toBinaryString(door_status));
        drawInformationsLine(g2, 5, "avionics.get_cdu_source()","(bool) "+avionics.get_cdu_source());
        drawInformationsLine(g2, 6, "avionics.preferences.cdu_display_only()","(bool) "+ preferences.cdu_display_only());
        drawInformationsLine(g2, 7, "avionics.get_fms_type()","(int) "+ avionics.get_fms_type());
        drawInformationsLine(g2, 8, "avionics.is_qpac()","(bool) "+ avionics.is_qpac());
        drawInformationsLine(g2, 9, "avionics.is_jar_a320neo()","(bool) "+ avionics.is_jar_a320neo());
        int strick_pri = ((int) sim_data.get_sim_float(XPlaneSimDataRepository.XDUAL_INDICATORS));
        drawInformationsLine(g2, 10,"aircraft.stick_priority","(bin) "+ Integer.toBinaryString(strick_pri));
        drawInformationsLine(g2, 11,"avionics.get_cdu_side()","(int) "+ avionics.get_cdu_side());
        drawInformationsLine(g2, 12,"QpacMcduData.getLine(cdu_side,0)",QpacMcduData.getLine(avionics.get_cdu_side(),0));
        
	}
	
}
