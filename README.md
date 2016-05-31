# Primo Mendeley Plugin
A [PushTo plugin](https://developers.exlibrisgroup.com/primo/integrations/frontend/pushto) for sending Primo records to [Mendeley](https://www.mendeley.com).

## Features
This plugin dynamically constucts a landing page for Mendeley's [web importer](https://www.mendeley.com/import/) to ingest. The following [citation meta tags](https://www.mendeley.com/import/information-for-publishers/#web-importer-support) are currently supported:
- citation_title
- citation_authors
- citation_doi
- citation_journal_title
- citation_publisher
- citation_date
- citation_isbn
- citation_issn
- citation_volume
- citation_issue
- citation_firstpage
- citation_lastpage
 
Mendeley's Web Importer does not support importing multiple records. If >1 records are sent to this adaptor (i.e. from the the e-shelf), the request is forwarded to the RIS exporter (which can be used to import the citations to [Mendeley Desktop](https://www.mendeley.com/download-mendeley-desktop/)).

## Installation
1. Download the latest [MendeleyProcess.jar file](../../releases), and 
2. put it in the following directory on your FE server(s): `$primo_dev/ng/primo/home/profile/search/pushTo/`. 
3. In the Back Office, update the `Pushto Adaptors` mapping table and `Keeping this item` code table as described [here](https://developers.exlibrisgroup.com/primo/integrations/frontend/pushto).
4. [Deploy All](https://knowledge.exlibrisgroup.com/Primo/Product_Documentation/Back_Office_Guide/Additional_Primo_Features/Deploy_All_Configuration_Settings).

### Removing the Mendeley action from the E-shelf page
Because the Menedley Web Importer cannot handle multiple records, you may wish to hide this action in the e-shlef page. Use the following CSS to do so:
```css
.EXLEShelfFolderContentsPushTo option[value="Mendeley"] {display:none !important;}
```

## Compiling
If you need to rebuild the jar file, the easiest way to do so is to install [Gradle](http://gradle.org/) and run `gradle build` from the project directory root. Before doings so, however, you'll need to download the following dependencies from your Primo server and place them in a `./lib` directory:
- primo-library-common-4.5.0.jar
- jaguar-client-4.5.0.jar
- primo-common-infra-4.5.0.jar

Optionally, to download the proprietary dependencies automagically, you could configure the `ssh.gradle` file to work with your Primo servers, and uncomment the following lines the `build.gradle` file: 
```groovy
apply from: 'ssh.gradle'
compileJava.dependsOn getPrimoLibs
```

## License
© 2016 Regents of the University. Of Minnesota. All rights reserved.
