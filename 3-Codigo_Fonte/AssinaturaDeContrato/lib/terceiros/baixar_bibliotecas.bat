@echo off
cd /d "%~dp0"
curl.exe -s -f -o poi-5.3.0.jar https://repo1.maven.org/maven2/org/apache/poi/poi/5.3.0/poi-5.3.0.jar && echo OK poi-5.3.0.jar || echo ERRO poi-5.3.0.jar
curl.exe -s -f -o poi-ooxml-5.3.0.jar https://repo1.maven.org/maven2/org/apache/poi/poi-ooxml/5.3.0/poi-ooxml-5.3.0.jar && echo OK poi-ooxml-5.3.0.jar || echo ERRO poi-ooxml-5.3.0.jar
curl.exe -s -f -o poi-ooxml-lite-5.3.0.jar https://repo1.maven.org/maven2/org/apache/poi/poi-ooxml-lite/5.3.0/poi-ooxml-lite-5.3.0.jar && echo OK poi-ooxml-lite-5.3.0.jar || echo ERRO poi-ooxml-lite-5.3.0.jar
curl.exe -s -f -o xmlbeans-5.2.1.jar https://repo1.maven.org/maven2/org/apache/xmlbeans/xmlbeans/5.2.1/xmlbeans-5.2.1.jar && echo OK xmlbeans-5.2.1.jar || echo ERRO xmlbeans-5.2.1.jar
curl.exe -s -f -o commons-compress-1.26.2.jar https://repo1.maven.org/maven2/org/apache/commons/commons-compress/1.26.2/commons-compress-1.26.2.jar && echo OK commons-compress-1.26.2.jar || echo ERRO commons-compress-1.26.2.jar
curl.exe -s -f -o commons-io-2.16.1.jar https://repo1.maven.org/maven2/commons-io/commons-io/2.16.1/commons-io-2.16.1.jar && echo OK commons-io-2.16.1.jar || echo ERRO commons-io-2.16.1.jar
curl.exe -s -f -o commons-collections4-4.4.jar https://repo1.maven.org/maven2/org/apache/commons/commons-collections4/4.4/commons-collections4-4.4.jar && echo OK commons-collections4-4.4.jar || echo ERRO commons-collections4-4.4.jar
curl.exe -s -f -o commons-codec-1.17.0.jar https://repo1.maven.org/maven2/commons-codec/commons-codec/1.17.0/commons-codec-1.17.0.jar && echo OK commons-codec-1.17.0.jar || echo ERRO commons-codec-1.17.0.jar
curl.exe -s -f -o commons-lang3-3.14.0.jar https://repo1.maven.org/maven2/org/apache/commons/commons-lang3/3.14.0/commons-lang3-3.14.0.jar && echo OK commons-lang3-3.14.0.jar || echo ERRO commons-lang3-3.14.0.jar
curl.exe -s -f -o log4j-api-2.23.1.jar https://repo1.maven.org/maven2/org/apache/logging/log4j/log4j-api/2.23.1/log4j-api-2.23.1.jar && echo OK log4j-api-2.23.1.jar || echo ERRO log4j-api-2.23.1.jar
curl.exe -s -f -o SparseBitSet-1.3.jar https://repo1.maven.org/maven2/com/zaxxer/SparseBitSet/1.3/SparseBitSet-1.3.jar && echo OK SparseBitSet-1.3.jar || echo ERRO SparseBitSet-1.3.jar
curl.exe -s -f -o commons-math3-3.6.1.jar https://repo1.maven.org/maven2/org/apache/commons/commons-math3/3.6.1/commons-math3-3.6.1.jar && echo OK commons-math3-3.6.1.jar || echo ERRO commons-math3-3.6.1.jar
curl.exe -s -f -o pdfbox-3.0.3.jar https://repo1.maven.org/maven2/org/apache/pdfbox/pdfbox/3.0.3/pdfbox-3.0.3.jar && echo OK pdfbox-3.0.3.jar || echo ERRO pdfbox-3.0.3.jar
curl.exe -s -f -o pdfbox-io-3.0.3.jar https://repo1.maven.org/maven2/org/apache/pdfbox/pdfbox-io/3.0.3/pdfbox-io-3.0.3.jar && echo OK pdfbox-io-3.0.3.jar || echo ERRO pdfbox-io-3.0.3.jar
curl.exe -s -f -o fontbox-3.0.3.jar https://repo1.maven.org/maven2/org/apache/pdfbox/fontbox/3.0.3/fontbox-3.0.3.jar && echo OK fontbox-3.0.3.jar || echo ERRO fontbox-3.0.3.jar
curl.exe -s -f -o commons-logging-1.3.4.jar https://repo1.maven.org/maven2/commons-logging/commons-logging/1.3.4/commons-logging-1.3.4.jar && echo OK commons-logging-1.3.4.jar || echo ERRO commons-logging-1.3.4.jar
