cmake_minimum_required(VERSION 3.27)
project(xhsi)

#find_package(PkgConfig REQUIRED)
#pkg_check_modules(sdl2 sdl2)
#pkg_check_modules(libgeek REQUIRED libgeek)

find_library(XPLM XPLM "${XPSDK}/Libraries/Mac")
find_library(XPWidgets XPWidgets "${XPSDK}/Libraries/Mac")

add_definitions(
    -DAPL
    "-I${XPSDK}/CHeaders/Widgets"
    "-I${XPSDK}/CHeaders/XPLM"
)

add_library(xhsi SHARED
commands.c             datarefs_pa_a320.c     datarefs_x_raas.c      packets.c              ufmc.c
datarefs.c             datarefs_pilotedge.c   datarefs_xdual.c       plugin.c               ui.c
datarefs_all_plugins.c datarefs_q400.c        datarefs_z737.c        qpac_msg.c             xfmc.c
datarefs_cl30.c        datarefs_qpac.c        endianess.c            receiver.c             xhsi_cdu.c
datarefs_ff_a320.c     datarefs_ufmc.c        jar_a320neo_msg.c      sender.c               xp11_cdu.c
datarefs_jar_a320neo.c datarefs_x737.c        net.c                  settings.c             z737_fmc.c
)
set_target_properties(xhsi PROPERTIES PREFIX "")
set_target_properties(xhsi PROPERTIES SUFFIX "")
set_target_properties(xhsi PROPERTIES LIBRARY_OUTPUT_NAME "XHSI.xpl")
set_target_properties(xhsi PROPERTIES LINK_FLAGS "-Wl,-F${XPSDK}/Libraries/Mac")
target_link_libraries(xhsi
        -Wl,-rpath -Wl,/usr/local/lib
        -L/usr/local/lib
        ${XPLM}
        ${XPWidgets}
)

