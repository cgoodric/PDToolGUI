#!/bin/sh
#
# runs the PDToolGUI jetty instance

APPS_INSTALL_DIR="/Users/cgoodric/dev/PDTool62"

java -Dapps.install.dir=$APPS_INSTALL_DIR -jar $APPS_INSTALL_DIR/gui/target/PDToolGUI-MAVEN_VERSION.jar server $APPS_INSTALL_DIR/gui/PDToolGUI.yml
