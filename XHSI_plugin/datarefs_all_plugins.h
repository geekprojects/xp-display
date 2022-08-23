/*
 * datarefs_aircrafts.h
 *
 * Goal : using only one call back to check each A/C datarefs
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

#ifndef DATAREFS_ALL_PLUGINS_H_
#define DATAREFS_ALL_PLUGINS_H_

/**
 * Check aircrafts datarefs every 5 seconds
 */
float checkPluginsDatarefsCallback(
        float	inElapsedSinceLastCall,
        float	inElapsedTimeSinceLastFlightLoop,
        int		inCounter,
        void *	inRefcon);

#endif /* DATAREFS_ALL_PLUGINS_H_ */
