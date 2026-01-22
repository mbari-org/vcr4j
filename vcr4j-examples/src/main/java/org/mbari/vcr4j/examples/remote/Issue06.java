package org.mbari.vcr4j.examples.remote;

/*-
 * #%L
 * vcr4j-examples
 * %%
 * Copyright (C) 2008 - 2026 Monterey Bay Aquarium Research Institute
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.remote.control.RemoteControl;
import org.mbari.vcr4j.remote.control.commands.OpenCmd;

import java.net.URL;
import java.util.List;
import java.util.UUID;

public class Issue06 {

    public static void main(String[] args) throws Exception {
        var urls = List.of(
                "http://varsdemo.mbari.org/media/M3/proxy/Ventana/2017/03/4003/V4003_20170301T165348.699Z_t1s1_1280_tc00000600_h264.mp4",
                "http://varsdemo.mbari.org/media/M3/proxy/Ventana/2017/03/4003/V4003_20170301T170948.799Z_t1s2_1280_tc00160505_h264.mp4",
                "http://varsdemo.mbari.org/media/M3/proxy/Ventana/2017/03/4003/V4003_20170301T172548.432Z_t1s3_1280_tc00320410_h264.mp4",
                "http://varsdemo.mbari.org/media/M3/proxy/Ventana/2017/03/4003/V4003_20170301T174148.566Z_t1s4_1280_tc00480315_h264.mp4",
                "http://varsdemo.mbari.org/media/M3/proxy/Ventana/2017/03/4003/V4003_20170301T180752.666Z_t2s1_1280_tc01020200_h264.mp4",
                "http://varsdemo.mbari.org/media/M3/proxy/Ventana/2017/03/4003/V4003_20170301T182353.474Z_t2s2_1280_tc01180105_h264.mp4",
                "http://varsdemo.mbari.org/media/M3/proxy/Ventana/2017/03/4003/V4003_20170301T183953.458Z_t2s3_1280_tc01340010_h264.mp4",
                "http://varsdemo.mbari.org/media/M3/proxy/Ventana/2017/03/4003/V4003_20170301T185553.933Z_t2s4_1280_tc01495915_h264.mp4",
                "http://varsdemo.mbari.org/media/M3/proxy/Ventana/2017/03/4003/V4003_20170301T191234.626Z_t3s1_1280_tc02050800_h264.mp4",
                "http://varsdemo.mbari.org/media/M3/proxy/Ventana/2017/03/4003/V4003_20170301T192833.733Z_t3s2_1280_tc02210705_h264.mp4",
                "http://varsdemo.mbari.org/media/M3/proxy/Ventana/2017/03/4003/V4003_20170301T194434.366Z_t3s3_1280_tc02370610_h264.mp4"
        );


        for (var url: urls) {
            var uuid = UUID.randomUUID();
            var remoteControl = new RemoteControl.Builder(uuid)
                    .port(5555)
                    .remotePort(8800)
                    .remoteHost("localhost")
                    .build()
                    .get();
            var io = remoteControl.getVideoIO();
            io.send(new OpenCmd(uuid, new URL(url)));
            Thread.sleep(3000);
            io.send(VideoCommands.PLAY);
            io.close();
        }
    }
}
