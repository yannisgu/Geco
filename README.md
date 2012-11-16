Geco 1.3
========
Copyright (c) 2008-2012 Simon Denier, Philipp Walker  
Contact: http://geco.webou.net  
[![Build Status](https://travis-ci.org/sdenier/Geco.png)](https://travis-ci.org/sdenier/Geco)

Geco is a lightweight application for managing orienteering races.  
It is written in Java and designed to be cross-platform.  

Geco comes with powerful features designed around its algorithm for automatic course detection: accurate trace, automatic entry before/after the race, race recreation from backup memory...

The Geco UI is designed to provide a lean user experience: navigate through stage workflow using the top tabs, and access data by direct manipulation.

Geco can handle multiple race formats: classic inline, free order, Orient'Show (penalty count, knockout qualifications).


About the 1.3
-------------

Geco 1.3 builds upon its course detection algorithm to provide more abilities for course management. Especially some new features provide a better support for training (training mode, import of ecard logs).

### Main changes
- 3 modes when reading ecards:
    - racing (classic mode)
    - training (handle multiple reads for the same ecard with different courses, create one entry per read without asking question)
    - register (handle onsite registration)
- Brand new merge wizard, replacing the old merge dialog from 1.0
    - displays more information
    - provides course detection
    - incremental search in registry and archive
- Auto course available by default
- Import of XML IOF V3 (beta) course file (OCAD 11...)
- New batch functions
    - import station logs and check DNS/Running
    - import ecard logs to recreate a race/training from the master station backup memory
    - export a referee log with all manual modifications
    - delete data

### Enhancements
- add an *Out of Time* status
- better arrangement for stats
- updated result format, including pace computation
- updated ticket format, customizable header/footer
- XML export of result by course (before only category was available)
- customizable reading modes (manual/auto/archive lookup)
- updated CSV result format to provide more data

### Fixed bugs
- Fix OE CSV export for CN, RouteGadget
- Fix import of course file generated by Purple Pen
- and other smaller fixes


Install
-------

Unzip the archive file (should be done already if you can read this file).

Geco runs with Java version 6 and above. It might run with Java version 5.
You can download a JRE (Java Runtime Environment) from http://www.java.com

You need the SPORTIdent drivers to read SI cards.

- Windows: download available at http://www.sportident.com/
- Linux: recent kernels recognize the chip used by SI station, so it’s plug’n’play.
- Mac OS X: it's possible to install and tweak a USB driver to get Mac OS X to recognize the station. See the FAQ in the documentation.


Launch
------

Double-click the jar file.

Geco 1.3 can load 1.2 data but not 1.1 data.


User documentation
------------------

Available under the `help/` folder in html format.

If you are experienced with orienteering softwares, you can jump-start using the application without the doc.
Geco UI is designed to be very usable (with some room for improvements): almost any actions available is visible, data accessible through direct manipulation, no hidden menus, no complicated workflow. 


Directory structure
-------------------

- `geco*.jar` - application
- `README.md` - this file
- `data/template/` - sample stage files editable with a spreadsheet application
- `data/modeles/` - french models for course-category associations
- `help/` - documentation in html format
- `licences/LICENSE` - license info (source code)
- `licences/gpl-2.0.txt` - license info (application)
- `licences/icu-license.txt` - more license info


Contribution
------------
If you want to contribute to Geco (whatever your skills are), you can! Just read [that page](http://geco.webou.net/geco/contribute.html) to see what you can do.

If you specifically want to develop, go read the [dev guidelines](https://github.com/sdenier/Geco/blob/master/README_DEV.md).


Thanks
------

Many thanks to Julien Thézé, Martin Flynn, and Jannik Laval for their technical help and debugging sessions.


License Information
-------------------

The Geco application is distributed under the GNU General Public License Version 2. See gpl-2.0.txt for details.

Original parts of this program are distributed under the MIT license. See LICENSE file for details.
Open-source code is available at http://bitbucket.org/sdenier/geco

JarClassLoader distributed under the GNU General Public License Version 2.
See http://www.jdotsoft.com/JarClassLoader.php

SIReader library kindly provided by Martin Flynn, many thanks to him!
Visit his software Òr at http://orienteering.ie/wiki/doku.php?id=or:index

SIReader uses the RXTX library, released under LGPL v 2.1 + Linking Over Controlled Interface.
See website for details http://www.rxtx.org/

Geco uses a subset of the ICU4J library, released under ICU License 1.8.1. See http://site.icu-project.org/

Icons come from the Crystal Project Icons, released under LGPL, designed by Everaldo Coelho.
See http://everaldo.com/crystal