/*
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
$(document).ready(function() {

    $(".click-title").mouseenter( function(    e){
        e.preventDefault();
        this.style.cursor="pointer";
    });
    $(".click-title").mousedown( function(event){
        event.preventDefault();
    });

    // Ugly code while this script is shared among several pages
    try{
        refreshHitsPerSecond(true);
    } catch(e){}
    try{
        refreshResponseTimeOverTime(true);
    } catch(e){}
    try{
        refreshResponseTimePercentiles();
    } catch(e){}
});


var responseTimePercentilesInfos = {
        data: {"result": {"minY": 197.0, "minX": 0.0, "maxY": 23604.0, "series": [{"data": [[0.0, 197.0], [0.1, 209.0], [0.2, 214.0], [0.3, 215.0], [0.4, 216.0], [0.5, 216.0], [0.6, 216.0], [0.7, 217.0], [0.8, 217.0], [0.9, 217.0], [1.0, 217.0], [1.1, 217.0], [1.2, 218.0], [1.3, 218.0], [1.4, 218.0], [1.5, 218.0], [1.6, 218.0], [1.7, 218.0], [1.8, 218.0], [1.9, 218.0], [2.0, 218.0], [2.1, 218.0], [2.2, 219.0], [2.3, 219.0], [2.4, 219.0], [2.5, 219.0], [2.6, 219.0], [2.7, 219.0], [2.8, 219.0], [2.9, 219.0], [3.0, 219.0], [3.1, 220.0], [3.2, 220.0], [3.3, 220.0], [3.4, 220.0], [3.5, 220.0], [3.6, 221.0], [3.7, 221.0], [3.8, 221.0], [3.9, 221.0], [4.0, 221.0], [4.1, 221.0], [4.2, 221.0], [4.3, 222.0], [4.4, 222.0], [4.5, 222.0], [4.6, 222.0], [4.7, 222.0], [4.8, 222.0], [4.9, 223.0], [5.0, 223.0], [5.1, 223.0], [5.2, 223.0], [5.3, 224.0], [5.4, 224.0], [5.5, 224.0], [5.6, 224.0], [5.7, 224.0], [5.8, 224.0], [5.9, 224.0], [6.0, 224.0], [6.1, 224.0], [6.2, 224.0], [6.3, 224.0], [6.4, 224.0], [6.5, 224.0], [6.6, 224.0], [6.7, 225.0], [6.8, 225.0], [6.9, 225.0], [7.0, 225.0], [7.1, 225.0], [7.2, 225.0], [7.3, 225.0], [7.4, 225.0], [7.5, 225.0], [7.6, 225.0], [7.7, 225.0], [7.8, 225.0], [7.9, 226.0], [8.0, 226.0], [8.1, 226.0], [8.2, 226.0], [8.3, 226.0], [8.4, 226.0], [8.5, 226.0], [8.6, 226.0], [8.7, 226.0], [8.8, 226.0], [8.9, 226.0], [9.0, 226.0], [9.1, 226.0], [9.2, 226.0], [9.3, 226.0], [9.4, 227.0], [9.5, 227.0], [9.6, 227.0], [9.7, 227.0], [9.8, 227.0], [9.9, 227.0], [10.0, 227.0], [10.1, 227.0], [10.2, 227.0], [10.3, 227.0], [10.4, 227.0], [10.5, 227.0], [10.6, 227.0], [10.7, 228.0], [10.8, 228.0], [10.9, 228.0], [11.0, 228.0], [11.1, 228.0], [11.2, 228.0], [11.3, 228.0], [11.4, 228.0], [11.5, 228.0], [11.6, 228.0], [11.7, 228.0], [11.8, 228.0], [11.9, 228.0], [12.0, 228.0], [12.1, 229.0], [12.2, 229.0], [12.3, 229.0], [12.4, 229.0], [12.5, 229.0], [12.6, 229.0], [12.7, 229.0], [12.8, 229.0], [12.9, 229.0], [13.0, 229.0], [13.1, 229.0], [13.2, 229.0], [13.3, 230.0], [13.4, 230.0], [13.5, 230.0], [13.6, 230.0], [13.7, 230.0], [13.8, 230.0], [13.9, 230.0], [14.0, 230.0], [14.1, 230.0], [14.2, 230.0], [14.3, 230.0], [14.4, 230.0], [14.5, 231.0], [14.6, 231.0], [14.7, 231.0], [14.8, 231.0], [14.9, 231.0], [15.0, 231.0], [15.1, 231.0], [15.2, 231.0], [15.3, 232.0], [15.4, 232.0], [15.5, 232.0], [15.6, 232.0], [15.7, 232.0], [15.8, 232.0], [15.9, 232.0], [16.0, 233.0], [16.1, 233.0], [16.2, 233.0], [16.3, 233.0], [16.4, 233.0], [16.5, 233.0], [16.6, 234.0], [16.7, 234.0], [16.8, 234.0], [16.9, 234.0], [17.0, 234.0], [17.1, 235.0], [17.2, 235.0], [17.3, 235.0], [17.4, 235.0], [17.5, 235.0], [17.6, 236.0], [17.7, 236.0], [17.8, 236.0], [17.9, 237.0], [18.0, 237.0], [18.1, 237.0], [18.2, 238.0], [18.3, 238.0], [18.4, 238.0], [18.5, 238.0], [18.6, 239.0], [18.7, 239.0], [18.8, 239.0], [18.9, 239.0], [19.0, 240.0], [19.1, 240.0], [19.2, 240.0], [19.3, 241.0], [19.4, 241.0], [19.5, 242.0], [19.6, 242.0], [19.7, 242.0], [19.8, 243.0], [19.9, 243.0], [20.0, 244.0], [20.1, 245.0], [20.2, 245.0], [20.3, 246.0], [20.4, 246.0], [20.5, 247.0], [20.6, 248.0], [20.7, 248.0], [20.8, 249.0], [20.9, 249.0], [21.0, 249.0], [21.1, 250.0], [21.2, 251.0], [21.3, 252.0], [21.4, 253.0], [21.5, 254.0], [21.6, 255.0], [21.7, 256.0], [21.8, 257.0], [21.9, 257.0], [22.0, 259.0], [22.1, 260.0], [22.2, 262.0], [22.3, 263.0], [22.4, 264.0], [22.5, 265.0], [22.6, 266.0], [22.7, 268.0], [22.8, 275.0], [22.9, 276.0], [23.0, 283.0], [23.1, 304.0], [23.2, 404.0], [23.3, 433.0], [23.4, 447.0], [23.5, 449.0], [23.6, 450.0], [23.7, 451.0], [23.8, 452.0], [23.9, 453.0], [24.0, 454.0], [24.1, 454.0], [24.2, 454.0], [24.3, 455.0], [24.4, 455.0], [24.5, 456.0], [24.6, 456.0], [24.7, 456.0], [24.8, 456.0], [24.9, 456.0], [25.0, 456.0], [25.1, 457.0], [25.2, 457.0], [25.3, 457.0], [25.4, 457.0], [25.5, 458.0], [25.6, 458.0], [25.7, 458.0], [25.8, 458.0], [25.9, 458.0], [26.0, 459.0], [26.1, 459.0], [26.2, 459.0], [26.3, 459.0], [26.4, 459.0], [26.5, 459.0], [26.6, 460.0], [26.7, 460.0], [26.8, 460.0], [26.9, 460.0], [27.0, 460.0], [27.1, 460.0], [27.2, 461.0], [27.3, 461.0], [27.4, 461.0], [27.5, 461.0], [27.6, 461.0], [27.7, 461.0], [27.8, 461.0], [27.9, 461.0], [28.0, 462.0], [28.1, 462.0], [28.2, 462.0], [28.3, 462.0], [28.4, 462.0], [28.5, 462.0], [28.6, 462.0], [28.7, 463.0], [28.8, 463.0], [28.9, 463.0], [29.0, 463.0], [29.1, 463.0], [29.2, 463.0], [29.3, 463.0], [29.4, 463.0], [29.5, 463.0], [29.6, 464.0], [29.7, 464.0], [29.8, 464.0], [29.9, 464.0], [30.0, 464.0], [30.1, 464.0], [30.2, 464.0], [30.3, 464.0], [30.4, 465.0], [30.5, 465.0], [30.6, 465.0], [30.7, 465.0], [30.8, 465.0], [30.9, 465.0], [31.0, 465.0], [31.1, 465.0], [31.2, 466.0], [31.3, 466.0], [31.4, 466.0], [31.5, 466.0], [31.6, 466.0], [31.7, 466.0], [31.8, 466.0], [31.9, 466.0], [32.0, 466.0], [32.1, 467.0], [32.2, 467.0], [32.3, 467.0], [32.4, 467.0], [32.5, 467.0], [32.6, 467.0], [32.7, 468.0], [32.8, 468.0], [32.9, 468.0], [33.0, 468.0], [33.1, 468.0], [33.2, 468.0], [33.3, 469.0], [33.4, 469.0], [33.5, 469.0], [33.6, 469.0], [33.7, 469.0], [33.8, 469.0], [33.9, 470.0], [34.0, 470.0], [34.1, 470.0], [34.2, 470.0], [34.3, 471.0], [34.4, 471.0], [34.5, 471.0], [34.6, 471.0], [34.7, 471.0], [34.8, 472.0], [34.9, 472.0], [35.0, 472.0], [35.1, 472.0], [35.2, 472.0], [35.3, 472.0], [35.4, 473.0], [35.5, 473.0], [35.6, 473.0], [35.7, 473.0], [35.8, 473.0], [35.9, 473.0], [36.0, 474.0], [36.1, 474.0], [36.2, 474.0], [36.3, 474.0], [36.4, 474.0], [36.5, 475.0], [36.6, 475.0], [36.7, 475.0], [36.8, 475.0], [36.9, 475.0], [37.0, 475.0], [37.1, 475.0], [37.2, 475.0], [37.3, 476.0], [37.4, 476.0], [37.5, 476.0], [37.6, 476.0], [37.7, 476.0], [37.8, 476.0], [37.9, 476.0], [38.0, 476.0], [38.1, 476.0], [38.2, 477.0], [38.3, 477.0], [38.4, 477.0], [38.5, 477.0], [38.6, 477.0], [38.7, 477.0], [38.8, 477.0], [38.9, 477.0], [39.0, 478.0], [39.1, 478.0], [39.2, 478.0], [39.3, 478.0], [39.4, 478.0], [39.5, 478.0], [39.6, 478.0], [39.7, 478.0], [39.8, 478.0], [39.9, 478.0], [40.0, 478.0], [40.1, 479.0], [40.2, 479.0], [40.3, 479.0], [40.4, 479.0], [40.5, 479.0], [40.6, 479.0], [40.7, 479.0], [40.8, 479.0], [40.9, 479.0], [41.0, 479.0], [41.1, 479.0], [41.2, 480.0], [41.3, 480.0], [41.4, 480.0], [41.5, 480.0], [41.6, 480.0], [41.7, 480.0], [41.8, 480.0], [41.9, 480.0], [42.0, 480.0], [42.1, 480.0], [42.2, 480.0], [42.3, 480.0], [42.4, 480.0], [42.5, 480.0], [42.6, 481.0], [42.7, 481.0], [42.8, 481.0], [42.9, 481.0], [43.0, 481.0], [43.1, 481.0], [43.2, 481.0], [43.3, 481.0], [43.4, 481.0], [43.5, 481.0], [43.6, 481.0], [43.7, 481.0], [43.8, 481.0], [43.9, 481.0], [44.0, 481.0], [44.1, 481.0], [44.2, 482.0], [44.3, 482.0], [44.4, 482.0], [44.5, 482.0], [44.6, 482.0], [44.7, 482.0], [44.8, 482.0], [44.9, 482.0], [45.0, 482.0], [45.1, 482.0], [45.2, 482.0], [45.3, 482.0], [45.4, 482.0], [45.5, 482.0], [45.6, 482.0], [45.7, 482.0], [45.8, 482.0], [45.9, 483.0], [46.0, 483.0], [46.1, 483.0], [46.2, 483.0], [46.3, 483.0], [46.4, 483.0], [46.5, 483.0], [46.6, 483.0], [46.7, 483.0], [46.8, 483.0], [46.9, 483.0], [47.0, 483.0], [47.1, 483.0], [47.2, 483.0], [47.3, 483.0], [47.4, 483.0], [47.5, 483.0], [47.6, 484.0], [47.7, 484.0], [47.8, 484.0], [47.9, 484.0], [48.0, 484.0], [48.1, 484.0], [48.2, 484.0], [48.3, 484.0], [48.4, 484.0], [48.5, 484.0], [48.6, 484.0], [48.7, 484.0], [48.8, 484.0], [48.9, 484.0], [49.0, 484.0], [49.1, 484.0], [49.2, 484.0], [49.3, 484.0], [49.4, 485.0], [49.5, 485.0], [49.6, 485.0], [49.7, 485.0], [49.8, 485.0], [49.9, 485.0], [50.0, 485.0], [50.1, 485.0], [50.2, 485.0], [50.3, 485.0], [50.4, 485.0], [50.5, 485.0], [50.6, 485.0], [50.7, 485.0], [50.8, 485.0], [50.9, 485.0], [51.0, 485.0], [51.1, 485.0], [51.2, 485.0], [51.3, 486.0], [51.4, 486.0], [51.5, 486.0], [51.6, 486.0], [51.7, 486.0], [51.8, 486.0], [51.9, 486.0], [52.0, 486.0], [52.1, 486.0], [52.2, 486.0], [52.3, 486.0], [52.4, 486.0], [52.5, 486.0], [52.6, 486.0], [52.7, 486.0], [52.8, 486.0], [52.9, 486.0], [53.0, 486.0], [53.1, 486.0], [53.2, 486.0], [53.3, 486.0], [53.4, 486.0], [53.5, 487.0], [53.6, 487.0], [53.7, 487.0], [53.8, 487.0], [53.9, 487.0], [54.0, 487.0], [54.1, 487.0], [54.2, 487.0], [54.3, 487.0], [54.4, 487.0], [54.5, 487.0], [54.6, 487.0], [54.7, 487.0], [54.8, 487.0], [54.9, 487.0], [55.0, 487.0], [55.1, 487.0], [55.2, 487.0], [55.3, 487.0], [55.4, 487.0], [55.5, 487.0], [55.6, 488.0], [55.7, 488.0], [55.8, 488.0], [55.9, 488.0], [56.0, 488.0], [56.1, 488.0], [56.2, 488.0], [56.3, 488.0], [56.4, 488.0], [56.5, 488.0], [56.6, 488.0], [56.7, 488.0], [56.8, 488.0], [56.9, 488.0], [57.0, 488.0], [57.1, 488.0], [57.2, 488.0], [57.3, 488.0], [57.4, 488.0], [57.5, 489.0], [57.6, 489.0], [57.7, 489.0], [57.8, 489.0], [57.9, 489.0], [58.0, 489.0], [58.1, 489.0], [58.2, 489.0], [58.3, 489.0], [58.4, 489.0], [58.5, 489.0], [58.6, 489.0], [58.7, 489.0], [58.8, 489.0], [58.9, 489.0], [59.0, 489.0], [59.1, 489.0], [59.2, 489.0], [59.3, 489.0], [59.4, 489.0], [59.5, 490.0], [59.6, 490.0], [59.7, 490.0], [59.8, 490.0], [59.9, 490.0], [60.0, 490.0], [60.1, 490.0], [60.2, 490.0], [60.3, 490.0], [60.4, 490.0], [60.5, 490.0], [60.6, 490.0], [60.7, 490.0], [60.8, 490.0], [60.9, 490.0], [61.0, 490.0], [61.1, 490.0], [61.2, 490.0], [61.3, 490.0], [61.4, 491.0], [61.5, 491.0], [61.6, 491.0], [61.7, 491.0], [61.8, 491.0], [61.9, 491.0], [62.0, 491.0], [62.1, 491.0], [62.2, 491.0], [62.3, 491.0], [62.4, 491.0], [62.5, 491.0], [62.6, 491.0], [62.7, 491.0], [62.8, 491.0], [62.9, 491.0], [63.0, 491.0], [63.1, 491.0], [63.2, 492.0], [63.3, 492.0], [63.4, 492.0], [63.5, 492.0], [63.6, 492.0], [63.7, 492.0], [63.8, 492.0], [63.9, 492.0], [64.0, 492.0], [64.1, 492.0], [64.2, 492.0], [64.3, 492.0], [64.4, 492.0], [64.5, 492.0], [64.6, 492.0], [64.7, 492.0], [64.8, 492.0], [64.9, 493.0], [65.0, 493.0], [65.1, 493.0], [65.2, 493.0], [65.3, 493.0], [65.4, 493.0], [65.5, 493.0], [65.6, 493.0], [65.7, 493.0], [65.8, 493.0], [65.9, 493.0], [66.0, 493.0], [66.1, 493.0], [66.2, 493.0], [66.3, 493.0], [66.4, 494.0], [66.5, 494.0], [66.6, 494.0], [66.7, 494.0], [66.8, 494.0], [66.9, 494.0], [67.0, 494.0], [67.1, 494.0], [67.2, 494.0], [67.3, 494.0], [67.4, 494.0], [67.5, 494.0], [67.6, 494.0], [67.7, 494.0], [67.8, 494.0], [67.9, 494.0], [68.0, 494.0], [68.1, 495.0], [68.2, 495.0], [68.3, 495.0], [68.4, 495.0], [68.5, 495.0], [68.6, 495.0], [68.7, 495.0], [68.8, 495.0], [68.9, 495.0], [69.0, 495.0], [69.1, 495.0], [69.2, 495.0], [69.3, 495.0], [69.4, 495.0], [69.5, 495.0], [69.6, 495.0], [69.7, 496.0], [69.8, 496.0], [69.9, 496.0], [70.0, 496.0], [70.1, 496.0], [70.2, 496.0], [70.3, 496.0], [70.4, 496.0], [70.5, 496.0], [70.6, 496.0], [70.7, 496.0], [70.8, 496.0], [70.9, 496.0], [71.0, 496.0], [71.1, 496.0], [71.2, 496.0], [71.3, 497.0], [71.4, 497.0], [71.5, 497.0], [71.6, 497.0], [71.7, 497.0], [71.8, 497.0], [71.9, 497.0], [72.0, 497.0], [72.1, 497.0], [72.2, 497.0], [72.3, 497.0], [72.4, 497.0], [72.5, 497.0], [72.6, 497.0], [72.7, 498.0], [72.8, 498.0], [72.9, 498.0], [73.0, 498.0], [73.1, 498.0], [73.2, 498.0], [73.3, 498.0], [73.4, 498.0], [73.5, 498.0], [73.6, 498.0], [73.7, 498.0], [73.8, 498.0], [73.9, 498.0], [74.0, 498.0], [74.1, 498.0], [74.2, 498.0], [74.3, 498.0], [74.4, 499.0], [74.5, 499.0], [74.6, 499.0], [74.7, 499.0], [74.8, 499.0], [74.9, 499.0], [75.0, 499.0], [75.1, 499.0], [75.2, 499.0], [75.3, 499.0], [75.4, 499.0], [75.5, 499.0], [75.6, 499.0], [75.7, 499.0], [75.8, 500.0], [75.9, 500.0], [76.0, 500.0], [76.1, 500.0], [76.2, 500.0], [76.3, 500.0], [76.4, 500.0], [76.5, 500.0], [76.6, 500.0], [76.7, 500.0], [76.8, 500.0], [76.9, 500.0], [77.0, 500.0], [77.1, 501.0], [77.2, 501.0], [77.3, 501.0], [77.4, 501.0], [77.5, 501.0], [77.6, 501.0], [77.7, 501.0], [77.8, 501.0], [77.9, 501.0], [78.0, 501.0], [78.1, 501.0], [78.2, 501.0], [78.3, 501.0], [78.4, 501.0], [78.5, 502.0], [78.6, 502.0], [78.7, 502.0], [78.8, 502.0], [78.9, 502.0], [79.0, 502.0], [79.1, 502.0], [79.2, 502.0], [79.3, 502.0], [79.4, 502.0], [79.5, 502.0], [79.6, 502.0], [79.7, 503.0], [79.8, 503.0], [79.9, 503.0], [80.0, 503.0], [80.1, 503.0], [80.2, 503.0], [80.3, 503.0], [80.4, 503.0], [80.5, 504.0], [80.6, 504.0], [80.7, 504.0], [80.8, 504.0], [80.9, 504.0], [81.0, 504.0], [81.1, 504.0], [81.2, 504.0], [81.3, 504.0], [81.4, 504.0], [81.5, 505.0], [81.6, 505.0], [81.7, 505.0], [81.8, 505.0], [81.9, 505.0], [82.0, 505.0], [82.1, 505.0], [82.2, 505.0], [82.3, 505.0], [82.4, 506.0], [82.5, 506.0], [82.6, 506.0], [82.7, 506.0], [82.8, 506.0], [82.9, 506.0], [83.0, 506.0], [83.1, 506.0], [83.2, 507.0], [83.3, 507.0], [83.4, 507.0], [83.5, 507.0], [83.6, 507.0], [83.7, 507.0], [83.8, 507.0], [83.9, 508.0], [84.0, 508.0], [84.1, 508.0], [84.2, 508.0], [84.3, 508.0], [84.4, 508.0], [84.5, 508.0], [84.6, 508.0], [84.7, 509.0], [84.8, 509.0], [84.9, 509.0], [85.0, 509.0], [85.1, 509.0], [85.2, 510.0], [85.3, 510.0], [85.4, 510.0], [85.5, 510.0], [85.6, 511.0], [85.7, 511.0], [85.8, 511.0], [85.9, 511.0], [86.0, 512.0], [86.1, 512.0], [86.2, 512.0], [86.3, 512.0], [86.4, 513.0], [86.5, 513.0], [86.6, 513.0], [86.7, 514.0], [86.8, 514.0], [86.9, 514.0], [87.0, 515.0], [87.1, 515.0], [87.2, 515.0], [87.3, 516.0], [87.4, 516.0], [87.5, 516.0], [87.6, 517.0], [87.7, 517.0], [87.8, 517.0], [87.9, 518.0], [88.0, 519.0], [88.1, 519.0], [88.2, 520.0], [88.3, 520.0], [88.4, 521.0], [88.5, 521.0], [88.6, 522.0], [88.7, 523.0], [88.8, 524.0], [88.9, 525.0], [89.0, 527.0], [89.1, 528.0], [89.2, 530.0], [89.3, 531.0], [89.4, 535.0], [89.5, 536.0], [89.6, 538.0], [89.7, 540.0], [89.8, 542.0], [89.9, 551.0], [90.0, 560.0], [90.1, 564.0], [90.2, 568.0], [90.3, 573.0], [90.4, 577.0], [90.5, 582.0], [90.6, 585.0], [90.7, 596.0], [90.8, 603.0], [90.9, 629.0], [91.0, 646.0], [91.1, 652.0], [91.2, 663.0], [91.3, 671.0], [91.4, 675.0], [91.5, 677.0], [91.6, 685.0], [91.7, 703.0], [91.8, 715.0], [91.9, 729.0], [92.0, 772.0], [92.1, 914.0], [92.2, 920.0], [92.3, 923.0], [92.4, 924.0], [92.5, 927.0], [92.6, 928.0], [92.7, 929.0], [92.8, 931.0], [92.9, 932.0], [93.0, 933.0], [93.1, 933.0], [93.2, 934.0], [93.3, 935.0], [93.4, 937.0], [93.5, 937.0], [93.6, 938.0], [93.7, 939.0], [93.8, 939.0], [93.9, 940.0], [94.0, 941.0], [94.1, 942.0], [94.2, 943.0], [94.3, 943.0], [94.4, 944.0], [94.5, 944.0], [94.6, 945.0], [94.7, 946.0], [94.8, 946.0], [94.9, 947.0], [95.0, 948.0], [95.1, 948.0], [95.2, 948.0], [95.3, 949.0], [95.4, 951.0], [95.5, 951.0], [95.6, 952.0], [95.7, 952.0], [95.8, 953.0], [95.9, 953.0], [96.0, 953.0], [96.1, 954.0], [96.2, 954.0], [96.3, 955.0], [96.4, 955.0], [96.5, 956.0], [96.6, 956.0], [96.7, 957.0], [96.8, 958.0], [96.9, 958.0], [97.0, 959.0], [97.1, 959.0], [97.2, 960.0], [97.3, 961.0], [97.4, 962.0], [97.5, 962.0], [97.6, 963.0], [97.7, 964.0], [97.8, 964.0], [97.9, 964.0], [98.0, 965.0], [98.1, 966.0], [98.2, 967.0], [98.3, 967.0], [98.4, 968.0], [98.5, 968.0], [98.6, 970.0], [98.7, 970.0], [98.8, 972.0], [98.9, 973.0], [99.0, 974.0], [99.1, 975.0], [99.2, 976.0], [99.3, 977.0], [99.4, 979.0], [99.5, 982.0], [99.6, 984.0], [99.7, 994.0], [99.8, 1096.0], [99.9, 1475.0], [100.0, 23604.0]], "isOverall": false, "label": "Dummy_API", "isController": false}], "supportsControllersDiscrimination": true, "maxX": 100.0, "title": "Response Time Percentiles"}},
        getOptions: function() {
            return {
                series: {
                    points: { show: false }
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendResponseTimePercentiles'
                },
                xaxis: {
                    tickDecimals: 1,
                    axisLabel: "Percentiles",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Percentile value in ms",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s : %x.2 percentile was %y ms"
                },
                selection: { mode: "xy" },
            };
        },
        createGraph: function() {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesResponseTimePercentiles"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotResponseTimesPercentiles"), dataset, options);
            // setup overview
            $.plot($("#overviewResponseTimesPercentiles"), dataset, prepareOverviewOptions(options));
        }
};

/**
 * @param elementId Id of element where we display message
 */
function setEmptyGraph(elementId) {
    $(function() {
        $(elementId).text("No graph series with filter="+seriesFilter);
    });
}

// Response times percentiles
function refreshResponseTimePercentiles() {
    var infos = responseTimePercentilesInfos;
    prepareSeries(infos.data);
    if(infos.data.result.series.length == 0) {
        setEmptyGraph("#bodyResponseTimePercentiles");
        return;
    }
    if (isGraph($("#flotResponseTimesPercentiles"))){
        infos.createGraph();
    } else {
        var choiceContainer = $("#choicesResponseTimePercentiles");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotResponseTimesPercentiles", "#overviewResponseTimesPercentiles");
        $('#bodyResponseTimePercentiles .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
}

var responseTimeDistributionInfos = {
        data: {"result": {"minY": 1.0, "minX": 100.0, "maxY": 2761.0, "series": [{"data": [[600.0, 48.0], [700.0, 19.0], [800.0, 3.0], [200.0, 1209.0], [900.0, 402.0], [1000.0, 3.0], [1100.0, 2.0], [1200.0, 1.0], [300.0, 7.0], [1300.0, 1.0], [1400.0, 3.0], [23600.0, 1.0], [1500.0, 2.0], [400.0, 2761.0], [100.0, 2.0], [500.0, 787.0]], "isOverall": false, "label": "Dummy_API", "isController": false}], "supportsControllersDiscrimination": true, "granularity": 100, "maxX": 23600.0, "title": "Response Time Distribution"}},
        getOptions: function() {
            var granularity = this.data.result.granularity;
            return {
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendResponseTimeDistribution'
                },
                xaxis:{
                    axisLabel: "Response times in ms",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Number of responses",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                bars : {
                    show: true,
                    barWidth: this.data.result.granularity
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: function(label, xval, yval, flotItem){
                        return yval + " responses for " + label + " were between " + xval + " and " + (xval + granularity) + " ms";
                    }
                }
            };
        },
        createGraph: function() {
            var data = this.data;
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotResponseTimeDistribution"), prepareData(data.result.series, $("#choicesResponseTimeDistribution")), options);
        }

};

// Response time distribution
function refreshResponseTimeDistribution() {
    var infos = responseTimeDistributionInfos;
    prepareSeries(infos.data);
    if(infos.data.result.series.length == 0) {
        setEmptyGraph("#bodyResponseTimeDistribution");
        return;
    }
    if (isGraph($("#flotResponseTimeDistribution"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesResponseTimeDistribution");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        $('#footerResponseTimeDistribution .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};


var syntheticResponseTimeDistributionInfos = {
        data: {"result": {"minY": 1.0, "minX": 0.0, "ticks": [[0, "Requests having \nresponse time <= 500ms"], [1, "Requests having \nresponse time > 500ms and <= 1,500ms"], [2, "Requests having \nresponse time > 1,500ms"], [3, "Requests in error"]], "maxY": 4019.0, "series": [{"data": [[0.0, 4019.0]], "color": "#9ACD32", "isOverall": false, "label": "Requests having \nresponse time <= 500ms", "isController": false}, {"data": [[1.0, 1205.0]], "color": "yellow", "isOverall": false, "label": "Requests having \nresponse time > 500ms and <= 1,500ms", "isController": false}, {"data": [[2.0, 1.0]], "color": "orange", "isOverall": false, "label": "Requests having \nresponse time > 1,500ms", "isController": false}, {"data": [[3.0, 26.0]], "color": "#FF6347", "isOverall": false, "label": "Requests in error", "isController": false}], "supportsControllersDiscrimination": false, "maxX": 3.0, "title": "Synthetic Response Times Distribution"}},
        getOptions: function() {
            return {
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendSyntheticResponseTimeDistribution'
                },
                xaxis:{
                    axisLabel: "Response times ranges",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                    tickLength:0,
                    min:-0.5,
                    max:3.5
                },
                yaxis: {
                    axisLabel: "Number of responses",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                bars : {
                    show: true,
                    align: "center",
                    barWidth: 0.25,
                    fill:.75
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: function(label, xval, yval, flotItem){
                        return yval + " " + label;
                    }
                }
            };
        },
        createGraph: function() {
            var data = this.data;
            var options = this.getOptions();
            prepareOptions(options, data);
            options.xaxis.ticks = data.result.ticks;
            $.plot($("#flotSyntheticResponseTimeDistribution"), prepareData(data.result.series, $("#choicesSyntheticResponseTimeDistribution")), options);
        }

};

// Response time distribution
function refreshSyntheticResponseTimeDistribution() {
    var infos = syntheticResponseTimeDistributionInfos;
    prepareSeries(infos.data, true);
    if (isGraph($("#flotSyntheticResponseTimeDistribution"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesSyntheticResponseTimeDistribution");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        $('#footerSyntheticResponseTimeDistribution .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var activeThreadsOverTimeInfos = {
        data: {"result": {"minY": 9.948802642444273, "minX": 1.74219294E12, "maxY": 26.35304595653725, "series": [{"data": [[1.74219294E12, 9.948802642444273]], "isOverall": false, "label": "Scenario2", "isController": false}, {"data": [[1.742193E12, 26.35304595653725], [1.74219294E12, 17.752797558494404], [1.74219306E12, 13.995999999999995]], "isOverall": false, "label": "Scenario1", "isController": false}], "supportsControllersDiscrimination": false, "granularity": 60000, "maxX": 1.74219306E12, "title": "Active Threads Over Time"}},
        getOptions: function() {
            return {
                series: {
                    stack: true,
                    lines: {
                        show: true,
                        fill: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Number of active threads",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20
                },
                legend: {
                    noColumns: 6,
                    show: true,
                    container: '#legendActiveThreadsOverTime'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                selection: {
                    mode: 'xy'
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s : At %x there were %y active threads"
                }
            };
        },
        createGraph: function() {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesActiveThreadsOverTime"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotActiveThreadsOverTime"), dataset, options);
            // setup overview
            $.plot($("#overviewActiveThreadsOverTime"), dataset, prepareOverviewOptions(options));
        }
};

// Active Threads Over Time
function refreshActiveThreadsOverTime(fixTimestamps) {
    var infos = activeThreadsOverTimeInfos;
    prepareSeries(infos.data);
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 19800000);
    }
    if(isGraph($("#flotActiveThreadsOverTime"))) {
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesActiveThreadsOverTime");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotActiveThreadsOverTime", "#overviewActiveThreadsOverTime");
        $('#footerActiveThreadsOverTime .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var timeVsThreadsInfos = {
        data: {"result": {"minY": 257.13367609254476, "minX": 1.0, "maxY": 736.1318681318679, "series": [{"data": [[2.0, 463.0], [3.0, 477.75], [4.0, 474.0], [5.0, 483.3333333333333], [6.0, 477.2], [7.0, 644.5833333333334], [8.0, 486.99999999999994], [9.0, 490.4545454545455], [10.0, 257.13367609254476], [11.0, 343.74025974025966], [12.0, 356.78481012658244], [13.0, 385.923913043478], [14.0, 356.4137931034483], [15.0, 387.378640776699], [16.0, 375.8514851485149], [1.0, 497.0], [17.0, 380.69902912621353], [18.0, 387.7924528301887], [19.0, 396.9927007299268], [20.0, 409.1388704318931], [21.0, 497.6352941176471], [22.0, 736.1318681318679], [23.0, 496.05063291139237], [24.0, 487.48214285714283], [25.0, 492.2056074766356], [26.0, 521.021978021978], [27.0, 489.6454545454544], [28.0, 567.8426966292134], [29.0, 569.7950819672132], [30.0, 608.3750819672142]], "isOverall": false, "label": "Dummy_API", "isController": false}, {"data": [[22.10893163207005, 473.3904018282232]], "isOverall": false, "label": "Dummy_API-Aggregated", "isController": false}], "supportsControllersDiscrimination": true, "maxX": 30.0, "title": "Time VS Threads"}},
        getOptions: function() {
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    axisLabel: "Number of active threads",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Average response times in ms",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20
                },
                legend: { noColumns: 2,show: true, container: '#legendTimeVsThreads' },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s: At %x.2 active threads, Average response time was %y.2 ms"
                }
            };
        },
        createGraph: function() {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesTimeVsThreads"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotTimesVsThreads"), dataset, options);
            // setup overview
            $.plot($("#overviewTimesVsThreads"), dataset, prepareOverviewOptions(options));
        }
};

// Time vs threads
function refreshTimeVsThreads(){
    var infos = timeVsThreadsInfos;
    prepareSeries(infos.data);
    if(infos.data.result.series.length == 0) {
        setEmptyGraph("#bodyTimeVsThreads");
        return;
    }
    if(isGraph($("#flotTimesVsThreads"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesTimeVsThreads");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotTimesVsThreads", "#overviewTimesVsThreads");
        $('#footerTimeVsThreads .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var bytesThroughputOverTimeInfos = {
        data : {"result": {"minY": 1005.9, "minX": 1.74219294E12, "maxY": 68613.91666666667, "series": [{"data": [[1.742193E12, 68613.91666666667], [1.74219294E12, 53181.13333333333], [1.74219306E12, 6021.683333333333]], "isOverall": false, "label": "Bytes received per second", "isController": false}, {"data": [[1.742193E12, 11276.4], [1.74219294E12, 8953.55], [1.74219306E12, 1005.9]], "isOverall": false, "label": "Bytes sent per second", "isController": false}], "supportsControllersDiscrimination": false, "granularity": 60000, "maxX": 1.74219306E12, "title": "Bytes Throughput Over Time"}},
        getOptions : function(){
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity) ,
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Bytes / sec",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendBytesThroughputOverTime'
                },
                selection: {
                    mode: "xy"
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s at %x was %y"
                }
            };
        },
        createGraph : function() {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesBytesThroughputOverTime"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotBytesThroughputOverTime"), dataset, options);
            // setup overview
            $.plot($("#overviewBytesThroughputOverTime"), dataset, prepareOverviewOptions(options));
        }
};

// Bytes throughput Over Time
function refreshBytesThroughputOverTime(fixTimestamps) {
    var infos = bytesThroughputOverTimeInfos;
    prepareSeries(infos.data);
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 19800000);
    }
    if(isGraph($("#flotBytesThroughputOverTime"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesBytesThroughputOverTime");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotBytesThroughputOverTime", "#overviewBytesThroughputOverTime");
        $('#footerBytesThroughputOverTime .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
}

var responseTimesOverTimeInfos = {
        data: {"result": {"minY": 366.32771194165906, "minX": 1.74219294E12, "maxY": 555.9743498396881, "series": [{"data": [[1.742193E12, 555.9743498396881], [1.74219294E12, 366.32771194165906], [1.74219306E12, 485.7200000000001]], "isOverall": false, "label": "Dummy_API", "isController": false}], "supportsControllersDiscrimination": true, "granularity": 60000, "maxX": 1.74219306E12, "title": "Response Time Over Time"}},
        getOptions: function(){
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Average response time in ms",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendResponseTimesOverTime'
                },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s : at %x Average response time was %y ms"
                }
            };
        },
        createGraph: function() {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesResponseTimesOverTime"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotResponseTimesOverTime"), dataset, options);
            // setup overview
            $.plot($("#overviewResponseTimesOverTime"), dataset, prepareOverviewOptions(options));
        }
};

// Response Times Over Time
function refreshResponseTimeOverTime(fixTimestamps) {
    var infos = responseTimesOverTimeInfos;
    prepareSeries(infos.data);
    if(infos.data.result.series.length == 0) {
        setEmptyGraph("#bodyResponseTimeOverTime");
        return;
    }
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 19800000);
    }
    if(isGraph($("#flotResponseTimesOverTime"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesResponseTimesOverTime");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotResponseTimesOverTime", "#overviewResponseTimesOverTime");
        $('#footerResponseTimesOverTime .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var latenciesOverTimeInfos = {
        data: {"result": {"minY": 355.54603463992737, "minX": 1.74219294E12, "maxY": 553.7452796579968, "series": [{"data": [[1.742193E12, 553.7452796579968], [1.74219294E12, 355.54603463992737], [1.74219306E12, 485.70799999999997]], "isOverall": false, "label": "Dummy_API", "isController": false}], "supportsControllersDiscrimination": true, "granularity": 60000, "maxX": 1.74219306E12, "title": "Latencies Over Time"}},
        getOptions: function() {
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Average response latencies in ms",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendLatenciesOverTime'
                },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s : at %x Average latency was %y ms"
                }
            };
        },
        createGraph: function () {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesLatenciesOverTime"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotLatenciesOverTime"), dataset, options);
            // setup overview
            $.plot($("#overviewLatenciesOverTime"), dataset, prepareOverviewOptions(options));
        }
};

// Latencies Over Time
function refreshLatenciesOverTime(fixTimestamps) {
    var infos = latenciesOverTimeInfos;
    prepareSeries(infos.data);
    if(infos.data.result.series.length == 0) {
        setEmptyGraph("#bodyLatenciesOverTime");
        return;
    }
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 19800000);
    }
    if(isGraph($("#flotLatenciesOverTime"))) {
        infos.createGraph();
    }else {
        var choiceContainer = $("#choicesLatenciesOverTime");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotLatenciesOverTime", "#overviewLatenciesOverTime");
        $('#footerLatenciesOverTime .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var connectTimeOverTimeInfos = {
        data: {"result": {"minY": 121.12260711030098, "minX": 1.74219294E12, "maxY": 257.4879999999999, "series": [{"data": [[1.742193E12, 213.7392233701465], [1.74219294E12, 121.12260711030098], [1.74219306E12, 257.4879999999999]], "isOverall": false, "label": "Dummy_API", "isController": false}], "supportsControllersDiscrimination": true, "granularity": 60000, "maxX": 1.74219306E12, "title": "Connect Time Over Time"}},
        getOptions: function() {
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getConnectTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Average Connect Time in ms",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendConnectTimeOverTime'
                },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s : at %x Average connect time was %y ms"
                }
            };
        },
        createGraph: function () {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesConnectTimeOverTime"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotConnectTimeOverTime"), dataset, options);
            // setup overview
            $.plot($("#overviewConnectTimeOverTime"), dataset, prepareOverviewOptions(options));
        }
};

// Connect Time Over Time
function refreshConnectTimeOverTime(fixTimestamps) {
    var infos = connectTimeOverTimeInfos;
    prepareSeries(infos.data);
    if(infos.data.result.series.length == 0) {
        setEmptyGraph("#bodyConnectTimeOverTime");
        return;
    }
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 19800000);
    }
    if(isGraph($("#flotConnectTimeOverTime"))) {
        infos.createGraph();
    }else {
        var choiceContainer = $("#choicesConnectTimeOverTime");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotConnectTimeOverTime", "#overviewConnectTimeOverTime");
        $('#footerConnectTimeOverTime .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var responseTimePercentilesOverTimeInfos = {
        data: {"result": {"minY": 214.0, "minX": 1.74219294E12, "maxY": 1502.0, "series": [{"data": [[1.742193E12, 1475.0], [1.74219294E12, 1502.0], [1.74219306E12, 677.0]], "isOverall": false, "label": "Max", "isController": false}, {"data": [[1.742193E12, 443.0], [1.74219294E12, 214.0], [1.74219306E12, 448.0]], "isOverall": false, "label": "Min", "isController": false}, {"data": [[1.742193E12, 944.7000000000003], [1.74219294E12, 499.0], [1.74219306E12, 505.0]], "isOverall": false, "label": "90th percentile", "isController": false}, {"data": [[1.742193E12, 977.0], [1.74219294E12, 715.5399999999995], [1.74219306E12, 592.2900000000002]], "isOverall": false, "label": "99th percentile", "isController": false}, {"data": [[1.742193E12, 493.0], [1.74219294E12, 251.0], [1.74219306E12, 482.5]], "isOverall": false, "label": "Median", "isController": false}, {"data": [[1.742193E12, 960.0], [1.74219294E12, 517.0], [1.74219306E12, 527.25]], "isOverall": false, "label": "95th percentile", "isController": false}], "supportsControllersDiscrimination": false, "granularity": 60000, "maxX": 1.74219306E12, "title": "Response Time Percentiles Over Time (successful requests only)"}},
        getOptions: function() {
            return {
                series: {
                    lines: {
                        show: true,
                        fill: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Response Time in ms",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendResponseTimePercentilesOverTime'
                },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s : at %x Response time was %y ms"
                }
            };
        },
        createGraph: function () {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesResponseTimePercentilesOverTime"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotResponseTimePercentilesOverTime"), dataset, options);
            // setup overview
            $.plot($("#overviewResponseTimePercentilesOverTime"), dataset, prepareOverviewOptions(options));
        }
};

// Response Time Percentiles Over Time
function refreshResponseTimePercentilesOverTime(fixTimestamps) {
    var infos = responseTimePercentilesOverTimeInfos;
    prepareSeries(infos.data);
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 19800000);
    }
    if(isGraph($("#flotResponseTimePercentilesOverTime"))) {
        infos.createGraph();
    }else {
        var choiceContainer = $("#choicesResponseTimePercentilesOverTime");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotResponseTimePercentilesOverTime", "#overviewResponseTimePercentilesOverTime");
        $('#footerResponseTimePercentilesOverTime .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};


var responseTimeVsRequestInfos = {
    data: {"result": {"minY": 218.0, "minX": 3.0, "maxY": 23604.0, "series": [{"data": [[32.0, 487.0], [33.0, 485.0], [35.0, 484.0], [34.0, 577.5], [36.0, 949.0], [38.0, 502.0], [39.0, 492.0], [41.0, 478.0], [40.0, 485.0], [43.0, 248.0], [42.0, 480.0], [44.0, 231.5], [45.0, 487.0], [46.0, 486.5], [47.0, 500.0], [49.0, 482.0], [48.0, 501.0], [3.0, 490.0], [51.0, 493.5], [50.0, 496.0], [52.0, 486.0], [54.0, 239.5], [55.0, 490.0], [56.0, 496.0], [59.0, 496.0], [58.0, 491.5], [61.0, 235.5], [60.0, 497.0], [62.0, 232.0], [63.0, 227.0], [64.0, 460.5], [65.0, 229.0], [6.0, 783.5], [7.0, 472.0], [11.0, 473.0], [15.0, 479.0], [19.0, 483.0], [21.0, 467.0], [24.0, 487.0], [25.0, 487.0], [26.0, 485.0], [30.0, 476.0], [31.0, 483.0]], "isOverall": false, "label": "Successes", "isController": false}, {"data": [[45.0, 23604.0], [47.0, 218.0], [51.0, 218.0]], "isOverall": false, "label": "Failures", "isController": false}], "supportsControllersDiscrimination": false, "granularity": 1000, "maxX": 65.0, "title": "Response Time Vs Request"}},
    getOptions: function() {
        return {
            series: {
                lines: {
                    show: false
                },
                points: {
                    show: true
                }
            },
            xaxis: {
                axisLabel: "Global number of requests per second",
                axisLabelUseCanvas: true,
                axisLabelFontSizePixels: 12,
                axisLabelFontFamily: 'Verdana, Arial',
                axisLabelPadding: 20,
            },
            yaxis: {
                axisLabel: "Median Response Time in ms",
                axisLabelUseCanvas: true,
                axisLabelFontSizePixels: 12,
                axisLabelFontFamily: 'Verdana, Arial',
                axisLabelPadding: 20,
            },
            legend: {
                noColumns: 2,
                show: true,
                container: '#legendResponseTimeVsRequest'
            },
            selection: {
                mode: 'xy'
            },
            grid: {
                hoverable: true // IMPORTANT! this is needed for tooltip to work
            },
            tooltip: true,
            tooltipOpts: {
                content: "%s : Median response time at %x req/s was %y ms"
            },
            colors: ["#9ACD32", "#FF6347"]
        };
    },
    createGraph: function () {
        var data = this.data;
        var dataset = prepareData(data.result.series, $("#choicesResponseTimeVsRequest"));
        var options = this.getOptions();
        prepareOptions(options, data);
        $.plot($("#flotResponseTimeVsRequest"), dataset, options);
        // setup overview
        $.plot($("#overviewResponseTimeVsRequest"), dataset, prepareOverviewOptions(options));

    }
};

// Response Time vs Request
function refreshResponseTimeVsRequest() {
    var infos = responseTimeVsRequestInfos;
    prepareSeries(infos.data);
    if (isGraph($("#flotResponseTimeVsRequest"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesResponseTimeVsRequest");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotResponseTimeVsRequest", "#overviewResponseTimeVsRequest");
        $('#footerResponseRimeVsRequest .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};


var latenciesVsRequestInfos = {
    data: {"result": {"minY": 0.0, "minX": 3.0, "maxY": 949.0, "series": [{"data": [[32.0, 487.0], [33.0, 485.0], [35.0, 484.0], [34.0, 577.5], [36.0, 949.0], [38.0, 502.0], [39.0, 492.0], [41.0, 478.0], [40.0, 485.0], [43.0, 248.0], [42.0, 480.0], [44.0, 231.5], [45.0, 487.0], [46.0, 486.5], [47.0, 500.0], [49.0, 482.0], [48.0, 501.0], [3.0, 490.0], [51.0, 493.5], [50.0, 496.0], [52.0, 486.0], [54.0, 239.5], [55.0, 490.0], [56.0, 496.0], [59.0, 496.0], [58.0, 491.5], [61.0, 235.5], [60.0, 497.0], [62.0, 232.0], [63.0, 227.0], [64.0, 460.5], [65.0, 229.0], [6.0, 782.5], [7.0, 472.0], [11.0, 473.0], [15.0, 479.0], [19.0, 483.0], [21.0, 467.0], [24.0, 487.0], [25.0, 487.0], [26.0, 485.0], [30.0, 476.0], [31.0, 483.0]], "isOverall": false, "label": "Successes", "isController": false}, {"data": [[45.0, 0.0], [47.0, 0.0], [51.0, 0.0]], "isOverall": false, "label": "Failures", "isController": false}], "supportsControllersDiscrimination": false, "granularity": 1000, "maxX": 65.0, "title": "Latencies Vs Request"}},
    getOptions: function() {
        return{
            series: {
                lines: {
                    show: false
                },
                points: {
                    show: true
                }
            },
            xaxis: {
                axisLabel: "Global number of requests per second",
                axisLabelUseCanvas: true,
                axisLabelFontSizePixels: 12,
                axisLabelFontFamily: 'Verdana, Arial',
                axisLabelPadding: 20,
            },
            yaxis: {
                axisLabel: "Median Latency in ms",
                axisLabelUseCanvas: true,
                axisLabelFontSizePixels: 12,
                axisLabelFontFamily: 'Verdana, Arial',
                axisLabelPadding: 20,
            },
            legend: { noColumns: 2,show: true, container: '#legendLatencyVsRequest' },
            selection: {
                mode: 'xy'
            },
            grid: {
                hoverable: true // IMPORTANT! this is needed for tooltip to work
            },
            tooltip: true,
            tooltipOpts: {
                content: "%s : Median Latency time at %x req/s was %y ms"
            },
            colors: ["#9ACD32", "#FF6347"]
        };
    },
    createGraph: function () {
        var data = this.data;
        var dataset = prepareData(data.result.series, $("#choicesLatencyVsRequest"));
        var options = this.getOptions();
        prepareOptions(options, data);
        $.plot($("#flotLatenciesVsRequest"), dataset, options);
        // setup overview
        $.plot($("#overviewLatenciesVsRequest"), dataset, prepareOverviewOptions(options));
    }
};

// Latencies vs Request
function refreshLatenciesVsRequest() {
        var infos = latenciesVsRequestInfos;
        prepareSeries(infos.data);
        if(isGraph($("#flotLatenciesVsRequest"))){
            infos.createGraph();
        }else{
            var choiceContainer = $("#choicesLatencyVsRequest");
            createLegend(choiceContainer, infos);
            infos.createGraph();
            setGraphZoomable("#flotLatenciesVsRequest", "#overviewLatenciesVsRequest");
            $('#footerLatenciesVsRequest .legendColorBox > div').each(function(i){
                $(this).clone().prependTo(choiceContainer.find("li").eq(i));
            });
        }
};

var hitsPerSecondInfos = {
        data: {"result": {"minY": 3.8333333333333335, "minX": 1.74219294E12, "maxY": 46.63333333333333, "series": [{"data": [[1.742193E12, 46.63333333333333], [1.74219294E12, 37.05], [1.74219306E12, 3.8333333333333335]], "isOverall": false, "label": "hitsPerSecond", "isController": false}], "supportsControllersDiscrimination": false, "granularity": 60000, "maxX": 1.74219306E12, "title": "Hits Per Second"}},
        getOptions: function() {
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Number of hits / sec",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: "#legendHitsPerSecond"
                },
                selection: {
                    mode : 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s at %x was %y.2 hits/sec"
                }
            };
        },
        createGraph: function createGraph() {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesHitsPerSecond"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotHitsPerSecond"), dataset, options);
            // setup overview
            $.plot($("#overviewHitsPerSecond"), dataset, prepareOverviewOptions(options));
        }
};

// Hits per second
function refreshHitsPerSecond(fixTimestamps) {
    var infos = hitsPerSecondInfos;
    prepareSeries(infos.data);
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 19800000);
    }
    if (isGraph($("#flotHitsPerSecond"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesHitsPerSecond");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotHitsPerSecond", "#overviewHitsPerSecond");
        $('#footerHitsPerSecond .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
}

var codesPerSecondInfos = {
        data: {"result": {"minY": 0.016666666666666666, "minX": 1.74219294E12, "maxY": 46.36666666666667, "series": [{"data": [[1.742193E12, 46.36666666666667], [1.74219294E12, 36.55], [1.74219306E12, 4.166666666666667]], "isOverall": false, "label": "201", "isController": false}, {"data": [[1.742193E12, 0.4166666666666667], [1.74219294E12, 0.016666666666666666]], "isOverall": false, "label": "Non HTTP response code: java.net.SocketException", "isController": false}], "supportsControllersDiscrimination": false, "granularity": 60000, "maxX": 1.74219306E12, "title": "Codes Per Second"}},
        getOptions: function(){
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Number of responses / sec",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: "#legendCodesPerSecond"
                },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "Number of Response Codes %s at %x was %y.2 responses / sec"
                }
            };
        },
    createGraph: function() {
        var data = this.data;
        var dataset = prepareData(data.result.series, $("#choicesCodesPerSecond"));
        var options = this.getOptions();
        prepareOptions(options, data);
        $.plot($("#flotCodesPerSecond"), dataset, options);
        // setup overview
        $.plot($("#overviewCodesPerSecond"), dataset, prepareOverviewOptions(options));
    }
};

// Codes per second
function refreshCodesPerSecond(fixTimestamps) {
    var infos = codesPerSecondInfos;
    prepareSeries(infos.data);
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 19800000);
    }
    if(isGraph($("#flotCodesPerSecond"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesCodesPerSecond");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotCodesPerSecond", "#overviewCodesPerSecond");
        $('#footerCodesPerSecond .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var transactionsPerSecondInfos = {
        data: {"result": {"minY": 0.016666666666666666, "minX": 1.74219294E12, "maxY": 46.36666666666667, "series": [{"data": [[1.742193E12, 46.36666666666667], [1.74219294E12, 36.55], [1.74219306E12, 4.166666666666667]], "isOverall": false, "label": "Dummy_API-success", "isController": false}, {"data": [[1.742193E12, 0.4166666666666667], [1.74219294E12, 0.016666666666666666]], "isOverall": false, "label": "Dummy_API-failure", "isController": false}], "supportsControllersDiscrimination": true, "granularity": 60000, "maxX": 1.74219306E12, "title": "Transactions Per Second"}},
        getOptions: function(){
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Number of transactions / sec",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: "#legendTransactionsPerSecond"
                },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s at %x was %y transactions / sec"
                }
            };
        },
    createGraph: function () {
        var data = this.data;
        var dataset = prepareData(data.result.series, $("#choicesTransactionsPerSecond"));
        var options = this.getOptions();
        prepareOptions(options, data);
        $.plot($("#flotTransactionsPerSecond"), dataset, options);
        // setup overview
        $.plot($("#overviewTransactionsPerSecond"), dataset, prepareOverviewOptions(options));
    }
};

// Transactions per second
function refreshTransactionsPerSecond(fixTimestamps) {
    var infos = transactionsPerSecondInfos;
    prepareSeries(infos.data);
    if(infos.data.result.series.length == 0) {
        setEmptyGraph("#bodyTransactionsPerSecond");
        return;
    }
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 19800000);
    }
    if(isGraph($("#flotTransactionsPerSecond"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesTransactionsPerSecond");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotTransactionsPerSecond", "#overviewTransactionsPerSecond");
        $('#footerTransactionsPerSecond .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var totalTPSInfos = {
        data: {"result": {"minY": 0.016666666666666666, "minX": 1.74219294E12, "maxY": 46.36666666666667, "series": [{"data": [[1.742193E12, 46.36666666666667], [1.74219294E12, 36.55], [1.74219306E12, 4.166666666666667]], "isOverall": false, "label": "Transaction-success", "isController": false}, {"data": [[1.742193E12, 0.4166666666666667], [1.74219294E12, 0.016666666666666666]], "isOverall": false, "label": "Transaction-failure", "isController": false}], "supportsControllersDiscrimination": true, "granularity": 60000, "maxX": 1.74219306E12, "title": "Total Transactions Per Second"}},
        getOptions: function(){
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Number of transactions / sec",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: "#legendTotalTPS"
                },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s at %x was %y transactions / sec"
                },
                colors: ["#9ACD32", "#FF6347"]
            };
        },
    createGraph: function () {
        var data = this.data;
        var dataset = prepareData(data.result.series, $("#choicesTotalTPS"));
        var options = this.getOptions();
        prepareOptions(options, data);
        $.plot($("#flotTotalTPS"), dataset, options);
        // setup overview
        $.plot($("#overviewTotalTPS"), dataset, prepareOverviewOptions(options));
    }
};

// Total Transactions per second
function refreshTotalTPS(fixTimestamps) {
    var infos = totalTPSInfos;
    // We want to ignore seriesFilter
    prepareSeries(infos.data, false, true);
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 19800000);
    }
    if(isGraph($("#flotTotalTPS"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesTotalTPS");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotTotalTPS", "#overviewTotalTPS");
        $('#footerTotalTPS .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

// Collapse the graph matching the specified DOM element depending the collapsed
// status
function collapse(elem, collapsed){
    if(collapsed){
        $(elem).parent().find(".fa-chevron-up").removeClass("fa-chevron-up").addClass("fa-chevron-down");
    } else {
        $(elem).parent().find(".fa-chevron-down").removeClass("fa-chevron-down").addClass("fa-chevron-up");
        if (elem.id == "bodyBytesThroughputOverTime") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshBytesThroughputOverTime(true);
            }
            document.location.href="#bytesThroughputOverTime";
        } else if (elem.id == "bodyLatenciesOverTime") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshLatenciesOverTime(true);
            }
            document.location.href="#latenciesOverTime";
        } else if (elem.id == "bodyCustomGraph") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshCustomGraph(true);
            }
            document.location.href="#responseCustomGraph";
        } else if (elem.id == "bodyConnectTimeOverTime") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshConnectTimeOverTime(true);
            }
            document.location.href="#connectTimeOverTime";
        } else if (elem.id == "bodyResponseTimePercentilesOverTime") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshResponseTimePercentilesOverTime(true);
            }
            document.location.href="#responseTimePercentilesOverTime";
        } else if (elem.id == "bodyResponseTimeDistribution") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshResponseTimeDistribution();
            }
            document.location.href="#responseTimeDistribution" ;
        } else if (elem.id == "bodySyntheticResponseTimeDistribution") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshSyntheticResponseTimeDistribution();
            }
            document.location.href="#syntheticResponseTimeDistribution" ;
        } else if (elem.id == "bodyActiveThreadsOverTime") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshActiveThreadsOverTime(true);
            }
            document.location.href="#activeThreadsOverTime";
        } else if (elem.id == "bodyTimeVsThreads") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshTimeVsThreads();
            }
            document.location.href="#timeVsThreads" ;
        } else if (elem.id == "bodyCodesPerSecond") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshCodesPerSecond(true);
            }
            document.location.href="#codesPerSecond";
        } else if (elem.id == "bodyTransactionsPerSecond") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshTransactionsPerSecond(true);
            }
            document.location.href="#transactionsPerSecond";
        } else if (elem.id == "bodyTotalTPS") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshTotalTPS(true);
            }
            document.location.href="#totalTPS";
        } else if (elem.id == "bodyResponseTimeVsRequest") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshResponseTimeVsRequest();
            }
            document.location.href="#responseTimeVsRequest";
        } else if (elem.id == "bodyLatenciesVsRequest") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshLatenciesVsRequest();
            }
            document.location.href="#latencyVsRequest";
        }
    }
}

/*
 * Activates or deactivates all series of the specified graph (represented by id parameter)
 * depending on checked argument.
 */
function toggleAll(id, checked){
    var placeholder = document.getElementById(id);

    var cases = $(placeholder).find(':checkbox');
    cases.prop('checked', checked);
    $(cases).parent().children().children().toggleClass("legend-disabled", !checked);

    var choiceContainer;
    if ( id == "choicesBytesThroughputOverTime"){
        choiceContainer = $("#choicesBytesThroughputOverTime");
        refreshBytesThroughputOverTime(false);
    } else if(id == "choicesResponseTimesOverTime"){
        choiceContainer = $("#choicesResponseTimesOverTime");
        refreshResponseTimeOverTime(false);
    }else if(id == "choicesResponseCustomGraph"){
        choiceContainer = $("#choicesResponseCustomGraph");
        refreshCustomGraph(false);
    } else if ( id == "choicesLatenciesOverTime"){
        choiceContainer = $("#choicesLatenciesOverTime");
        refreshLatenciesOverTime(false);
    } else if ( id == "choicesConnectTimeOverTime"){
        choiceContainer = $("#choicesConnectTimeOverTime");
        refreshConnectTimeOverTime(false);
    } else if ( id == "choicesResponseTimePercentilesOverTime"){
        choiceContainer = $("#choicesResponseTimePercentilesOverTime");
        refreshResponseTimePercentilesOverTime(false);
    } else if ( id == "choicesResponseTimePercentiles"){
        choiceContainer = $("#choicesResponseTimePercentiles");
        refreshResponseTimePercentiles();
    } else if(id == "choicesActiveThreadsOverTime"){
        choiceContainer = $("#choicesActiveThreadsOverTime");
        refreshActiveThreadsOverTime(false);
    } else if ( id == "choicesTimeVsThreads"){
        choiceContainer = $("#choicesTimeVsThreads");
        refreshTimeVsThreads();
    } else if ( id == "choicesSyntheticResponseTimeDistribution"){
        choiceContainer = $("#choicesSyntheticResponseTimeDistribution");
        refreshSyntheticResponseTimeDistribution();
    } else if ( id == "choicesResponseTimeDistribution"){
        choiceContainer = $("#choicesResponseTimeDistribution");
        refreshResponseTimeDistribution();
    } else if ( id == "choicesHitsPerSecond"){
        choiceContainer = $("#choicesHitsPerSecond");
        refreshHitsPerSecond(false);
    } else if(id == "choicesCodesPerSecond"){
        choiceContainer = $("#choicesCodesPerSecond");
        refreshCodesPerSecond(false);
    } else if ( id == "choicesTransactionsPerSecond"){
        choiceContainer = $("#choicesTransactionsPerSecond");
        refreshTransactionsPerSecond(false);
    } else if ( id == "choicesTotalTPS"){
        choiceContainer = $("#choicesTotalTPS");
        refreshTotalTPS(false);
    } else if ( id == "choicesResponseTimeVsRequest"){
        choiceContainer = $("#choicesResponseTimeVsRequest");
        refreshResponseTimeVsRequest();
    } else if ( id == "choicesLatencyVsRequest"){
        choiceContainer = $("#choicesLatencyVsRequest");
        refreshLatenciesVsRequest();
    }
    var color = checked ? "black" : "#818181";
    if(choiceContainer != null) {
        choiceContainer.find("label").each(function(){
            this.style.color = color;
        });
    }
}

