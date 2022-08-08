package org.mbari.vcr4j.remote.control.commands;

/**
 * The package contains specialized commands that can by send by a
 * {{@link org.mbari.vcr4j.remote.control.RVideoIO}}. Other commands that can be sent can be found
 * in {{@link org.mbari.vcr4j.commands.VideoCommands}}
 *
 * Each `Cmd` contains both a static {{@link org.mbari.vcr4j.remote.control.commands.RRequest}} as well
 * as a static {@link org.mbari.vcr4j.remote.control.commands.RResponse} class that can be
 * serialized/deserialized using Gson.
 *
 */