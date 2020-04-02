The stationxml-seed-converter automatically changes unit name casing during the conversion process from dataless seed to StationXML files. Blocklette 34 field 4 of the SEED data structure prescribes that unit names are "formatted as FORTRAN-like equations with all alphabetic characters in upper case" (SEED Manual). As the geophysical community moves from dataless SEED to StationXML file formats, IRIS suggests that FDSN-StationXML unit naming conventions should follow SI standards. To help with this transition, a unit mapper has been embedded into the stationxml-seed-converter. Unit names and symbols used in response metadata are accepted case insensitively (Right Column) and are mapped to SI unit naming conventions (Left Column), which are outlined in the table below.

 | SI Units | Dataless Unit (Case Insensitive)|
 |------|-------------|
 |   meter | meter, meters |
 |   m   | m |
 |   m/s  | m/s |
 |   m/s**2 | m/s**2 |
 |  centimeter | centimeter, centimeters |
 |  cm   | cm |
 |   cm/s  | cm/s |
 |   cm/s**2 | cm/s**2 |
 |  millimeter   | millimeter, millimeters |
 |   mm   | mm |
 |   mm/s  | mm/s |
 |   mm/s**2 | mm/s**2 |
 |   mm/hour | mm/hour |
 |   micrometer   | micrometer, micrometers |
 |   um   | um |
 |   um/s  | um/s |
 |   um/s**2 | um/s**2 |
 |   nanometer   | nanometer, nanometers |
 |   nm   | nm |
 |   nm/s  | nm/s |
 |   nm/s**2 | nm/s**2 |
 |   second | second, seconds |
 |   s   | s |
 |   millisecond | millisecond, milliseconds |
 |   ms   | ms |
 |   microsecond | microsecond, microseconds |
 |   us   | us |
 |   nanosecond | nanosecond, nanoseconds |
 |   ns   | ns |
 |   minute | minute, minutes |
 |   min  | min |
 |   hour  | hour, hours |
 |   radian | radian, radians |
 |   rad  | rad |
 |   microradian | microradian, microradians |
 |   urad  | urad |
 |   nanoradian | nanoradian, nanoradians |
 |   nrad  | nrad |
 |   rad/s | rad/s |
 |   rad/s**2 | rad/s**2 |
 |   degree | degree, degrees |
 |   deg  | deg |
 |   kelvin | kelvin |
 |   K   | k |
 |   celsius | celsius |
 |   degC  | degC |
 |   candela | candela |
 |   cd   | cd |
 |   pascal | pascal, pascals |
 |   Pa   | pa |
 |   kilopascal | kilopascal, kilopascals |
 |   kPa  | kPa |
 |   hectopascal | hectopascal, hectopascals |
 |   hPa  | hPa |
 |   bar  | bar, bars |
 |   millibar | millibar, millibars |
 |   mbar  | mbar |
 |   ampere | ampere, amperes |
 |   A   | a |
 |   milliamp | milliamp, milliamps |
 |   mA   | ma |
 |   volt  | volt, volts |
 |   V   | v |
 |   millivolt  | millivolt, millivolts |
 |   mV   | mv |
 |   microvolt  | microvolt, microvolts |
 |   uV   | uv |
 |   ohm  | ohm |
 |   hertz | hertz |
 |   Hz   | Hz |
 |   newton | newton, newtons |
 |   N   | n |
 |   joule | joule, joules |
 |   J   | j |
 |   tesla | tesla |
 |   T   | t |
 |   nanotesla | nanotesla |
 |   nT   | nt |
 |   strain | strain |
 |   m/m  | m/m |
 |   cm/cm | cm/cm |
 |   mm/mm | mm/mm |
 |   um/um | um/um |
 |   nm/nm | nm/nm |
 |   microstrain | microstrain |
 |   watt  | watt, watts |
 |   W   | w |
 |   milliwatt | milliwatt, milliwatts |
 |   mW   | mw |
 |   V/m  | v/m |
 |   W/m**2 | w/m**2 |
 |   gap  | gap |
 |   counts | counts |
 |   count | count |
 |   byte | byte, bytes |
 |   bit | bit, bits |
 |   bit/s | bit/s |
 |   reboot | reboot |
 |   percent | percent, % |
 |   number | number, numbers |
 |   unitless | unitless |
 |   unknown | unknown,'' ,' ' |
