Geco release 1.2
================
Copyright (c) 2008-2011 Simon Denier, Philipp Walker  
Contact: http://www.simondenier.eu

Geco is a lightweight application for managing orienteering races.  
It is written in Java and designed to be cross-platform (please report if you find platform issues).

Geco UI is designed to provide a lean user experience: navigate through stage workflow using the top tabs, and access data by direct manipulation.

Geco is also geared towards the Orient'Show format:

- it provides advanced functions to explain MPs and count penalties (including butterfly miss);
- it manages knockout qualifications.

About the 1.2 release
---------------------

Geco 1.2 is an intermediate release, which nonetheless ships with significant changes.
It paves the way for the future versions of Geco, providing some core changes in the UI and data model.

Here is the list of visible changes:

- Geco now explicitly handles the different formats of orienteering races: in particular it adapts the UI to each format (config options, custom tabs...).
Current formats include Classic inline, Orient'Show, and Free Order. Raid Orient'Alpin is a custom format but should be considered experimental.
- Reworked tab for configuration (each configuration item now has its own panel). 
- New launch wizard which enables to either open an existing stage (from an history of recent stages) or creates a new one for a given format.
- ecard number is no longer required and can be left empty.
- First german translation by Philipp Walker.
- LiveMap calibration updated with an intuitive UI (click on controls and map).
- Import template for category+course.
- IOF XML export for results (for RouteGadget and others).
- Some other minor changes in the UI.

- Change in data file format: Geco 1.2 will automatically convert 1.1 stage files to the new format.
Meaning also that Geco 1.1 can't load a 1.2 stage (use stage backups to be safe!).

### Fixed bugs in 1.2
- Course detection may return the wrong course in Orient'Show format (this happened when similar courses would both mark the runner trace as OK, even with MPs > 0)


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


User documentation
------------------

Available under the `help/` folder in html format.

If you are experienced with orienteering softwares, you can jump-start using the application without the doc.
Geco UI is designed to be very usable (with some room for improvements): almost any actions available is visible, data accessible through direct manipulation, no hidden menus, no complicated workflow. 


Directory structure
-------------------

- `geco*.jar` - application
- `README.md` - this file
- `LICENSE` - license info (source code)
- `gpl-2.0.txt` - license info (application)
- `icu-license.txt` - license info
- `data/template/` - sample stage files editable with a spreadsheet application 
- `help/` - documentation in html format


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