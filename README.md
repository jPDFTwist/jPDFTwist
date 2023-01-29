# jPDFTwist

jPDFTwist is a powerful, multi-functional tool for batch PDF tweaking, with the added capability of converting images to PDFs. Written entirely in JAVA Swing and built using gradle, jPDFTwist provides an easy and efficient way to manage and manipulate PDF files.

![MainTab](https://user-images.githubusercontent.com/6230644/215322434-6ee34c1d-0bda-4463-a40e-52d48301085e.png)

## Features

* Batch edit PDFs including:
  * Crop
  * Scale
  * Rotate
  * Watermark
  * Page numbering
  * Bookmark and annotations management
  * Page shuffling
  * Encrypt
  * Sign
* Merge multiple files (PDFs and images)
* Burst file to PDFs or Images
* Convert images to PDFs
* Export to including multi-page TIFFs
* etc...

## Disclaimer

It is important to note that the Adobe PDF specification (if legally binding in your jurisdiction) forbids removing permissions without having access to the password. The contributors of jPDFTwist do not have access to any passwords and cannot be held responsible for any actions taken using this tool. It is the user's responsibility to ensure they have the necessary permissions and rights before using jPDFTwist to edit or manipulate PDF files.

## Requirements and Installation

> To run jPDFTwist you need to have JAVA 8 installed on your machine.

1. Download the [latest release](https://github.com/xlance-github/jPDFTwist/releases) of jPDFTwist.
2. Unzip the downloaded file
3. Run the command `java -jar jPDFTwist-<replace version>.jar` to start the application

## Build from source

1. Download the [latest source code](https://github.com/xlance-github/jPDFTwist/archive/refs/heads/main.zip) of jPDFTwist.
2. Unzip the downloaded file
3. Run the command `gradle build` to build the project
4. Run the command `gradle run` to start the application

## Usage

jPDFTwist can be used to batch tweak PDFs, or convert images to PDFs.

For batch tweaking, simply select the PDF files you wish to manipulate and use the available tabs to apply your desired configuration.

To convert images to PDFs, select the image files you wish to convert and choose the output type on the "Output" tab.

The resulting PDFs/Images will be saved in the location specified at the "Output" tab.

# Support

If you have any issues or feature requests, please open an issue on the [issues page](https://github.com/xlance-github/jPDFTwist/issues) or contact the developers directly.

# Contributing

Special thanks for all the people who had helped this project so far:

* [Ed Victor](https://github.com/xlance-github) - Project Manager
* [Vasilis Naskos](https://github.com/vnaskos) - Lead Developer

If you would like to contribute to the development of jPDFTwist, please fork the repository and submit a pull request with your changes.

# License

jPDFTwist is licensed under the [AGPLv3](LICENSE.md)

# Acknowledgments

jPDFTwist is made possible with the help of the following open source projects:

* fork of [jPDFTweak](https://jpdftweak.sourceforge.net/) project from [Michael Schierl](https://sourceforge.net/u/schierlm/profile/)
