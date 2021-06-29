# DICOM - Teilaufgabe 1

Um DICOM-Dateien anzeigen zu können, müssen wir sie zunächst einlesen können. Zudem soll für die Diagnostik eine Kantendetektion angeboten werden - um diese kümmern wir uns direkt mit.

## Projektsetup

Bei dem DICOM-Format handelt es sich um ein komplexes Containerformat für Bilddateien (ein Überblick ist z.B. [hier](http://dicom.nema.org/medical/dicom/current/output/chtml/part10/chapter_7.html) zu finden). Um ein solch komplexes Dateiformat einzulesen, bietet sich die Verwendung einer existierenden Bibliothek an - in diesem Fall [dcm4che](https://www.dcm4che.org/). Um diese Bibliothek zu verwenden, müssen zunächst ein paar Einstellungen vorgenommen werden. Nehmen Sie diese bitte für ein besseres Verständnis selber vor - sie sind in der Aufgabenstellung noch nicht vorhanden!

### Maven-Module in build.gradle

In build.gradle finden Sie einen Bereich, in dem die verwendeten maven-Repositories definiert sind:

```java
repositories{
    mavenCentral()
}
```

In diesem Beispiel wird nur das mavenCentral-Plugin von Gradle verwendet, über welches das [entsprechende bei apache.org gehostete repository](https://repo.maven.apache.org/maven2/) eingebunden wird. Viele Pakete werden aber über eigene repositories angeboten, insbesondere größere Bilbiotheken hosten häufig ihre eigenen maven-repositories. Solche repositories können wie folgt eingebunden werden:

```java
repositories{
    mavenCentral()
    maven{
        url "http://repo1.com/maven2/"
    }
    maven{
        url "https://repo2.org/maven2/"
    }
    ...
}
```

Für das aktuelle Projekt werden die folgenden Module (zu definieren wie gewohnt in build.gradle unter dependencies) benötigt:

* org.dcm4che:dcm4che-core:5.23.1
* org.dcm4che:dcm4che-image:5.23.1
* org.dcm4che:dcm4che-imageio:5.23.1
* org.dcm4che:dcm4che-imageio-rle:5.23.1
* org.dcm4che:dcm4che-imageio-opencv:5.23.1

Diese Module und ihre Abhängigkeiten sind in den folgenden maven-repositories zu finden:

* https://www.dcm4che.org/maven2/
* https://raw.github.com/nroduit/mvn-repo/master/

### Native Bibliotheken

Für das Einlesen der in den DICOM-Dateien enthaltenen Bilddaten in diversen Formaten gibt es leider keine reinen Java-Pakete. Stattdessen wird die native [Open Computer Vision (OpenCV)-Bibliothek](https://opencv.org/) benötigt. Vorkompilierte und mit der jeweiligen dmc4che3-Version kompatible Versionen dieser Bibliothek finden sich in dem jeweiligen [binary distribution package von dcm4che](https://github.com/dcm4che/dcm4che/releases). In der dort verfügbaren zip-Datei (achten Sie beim Download darauf, die selbe Version zu verwenden, wie in den oben genannten dcm4che-Modulen) befindet sich ein Ordner "lib" mit Unterordnern für unterschiedliche Betriebssysteme. Sie benötigen alle in dem für Ihr Betriebssystem relevanten Unterordner vorhandenen .dll/.so/.jnilib-Dateien. Kopieren Sie diese in das lib-Verzeichnis des Projekts (aber fügen Sie sie bitte nicht zum git-repo hinzu). Sie müssen dann nur noch das Projekt so konfigurieren, dass diese Bibliotheken als Teil des library paths (eine Liste aller Verzeichnisse, in denen nach nativen Bibliotheken gesucht wird, falls eine solche geladen werden muss) gesehen werden. Dafür können Sie in gradle den run-Eintrag um folgende Zeile erweitern:

```java
    systemProperty "java.library.path", 'lib'
```

Nach diesen Anpassungen müssten Sie die dcm4che-Bibliothek in Ihrem Java-Projekt verwenden können.

## Einlesen von DICOM-Dateien

### Hintergrundinformationen

#### dcm4che für DICOM-Dateien

Der folgende Codeabschnitt zeigt Ihnen, wie die Bilddaten aus einer DICOM-Datei eingelesen werden können:

```java
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;

public class DICOMTest {
    public static void main(String[] args) {
        ImageIO.scanForPlugins();
        ImageReader ir = ImageIO.getImageReadersByFormatName("DICOM").next();
        DicomImageReadParam param = (DicomImageReadParam) ir.getDefaultReadParam();
        ImageInputStream iis = ImageIO.createImageInputStream(new File("data/angiogram1.DCM"));
        ir.setInput(iis);
        BufferedImage image = ir.read(1, param);
    }
}
```

Zunächst wird die ImageIO-Bibliothek angewiesen, nach plugins für Dateiformate zu suchen. Dabei wird die im classpath vorhandene dcm4che-Bibliothek gefunden und für das DICOM-Format registriert. Der entsprechenden ImageReader (eine abstrakte Klasse, die von allen ImageIO-Plugins implementiert werden muss) wird in der nächsten Zeile erstellt. Danach wird ein Objekt mit Standard-Parametern für das Einlesen von DICOM-Bildern erstellt - hier brauchen wir uns um keine Details kümmern, diese Standardparameter funktionieren für Standard-Anwendungsfälle hervorragend. 

Die nächsten beiden Zeilen erstellen einen InputStream für die Bilddatei - das ist wieder eine abstrakte Klasse, die verwendet werden kann, um Leseoperationen auf unterschiedlichen Datentypen zu implementieren, die zwar alle in irgendeiner Art Daten lesen und zur Verfügung stellen, aber beispielsweise unterschiedliche Sprung- und Suchoperationen zur Verfügung stellen können (z. B. im Fall von DICOM-Dateien das Springen zwischen unterschiedlichen Einträgen in der Datei). 

Abschließend wird das erste Bild aus der DICOM-Datei in ein BufferedImage eingelesen. Der erste Parameter in ```ir.read(1, param)``` zeigt an, welches der Bilder aus der Bildfolge eingelesen werden soll. Die frames sind aufsteigend lückenlos durchnummeriert, wie in einem Array. Wird versucht, ein frame einzulesen, das nicht vorhanden ist (also ein Index < 0 oder > Anzahl der vorhandenen frames), so wirft die read-Methode eine ```IndexOutOfBoundsException```.

#### Kantendetektion mit dem Sobel-Filter

Neben dem Einlesen von DICOM-Dateien soll auch eine Kantendetektion zur Unterstützung der Diagnostik stattfinden. Dafür verwenden wir einen einfachen Filter, den Sobel-Operator. Dieser addiert und subtrahiert die Werte der direkt über/unter (für die Detektion horizontaler Kanten) bzw. neben (für die Detektion vertikaler Kanten) einem Pixel liegenden Pixel so, dass sich bei starken Veränderungen der Frabintensität große Werte ergeben. Auf Flächen mit ungefähr gleichbleibender Intensität löschen sich hingegen die Werte der umliegenden Pixel aus.

Zur Veranschaulichung hier die Matrix, nach der die Summierung für die Detektion vertikaler Kanten durchgeführt wird:

```text
| 1 0 -1 |
| 2 0 -2 |
| 1 0 -1 |
```

Der Referenzpixel, für den die Kantendetektion durchgeführt wird, wird durch die 0 in der Mitte der Matrix repräsentiert. Die umliegenden Pixel werden mit den umliegenden Werten multipliziert, dann werden alle Werte summiert.

Die Pixelwerte auf der linken Seite werden also mit 2 (direkt links vom Referenzpixel) bzw. mit 1 (links oben und links unten vom Referenzpixel) multipliziert. Die Pixelwerte auf der rechten Seite werden umgekehrt mit -2 (direkt rechts vom Referenzpixel) bzw. mit -1 (rechts oben und rechts unten vom Referenzpixel) multipliziert.

Auf einer Fläche mit in etwa gleichbleibender Farbintensität löschen sich also die Werte der Pixel auf der linken Seite mit den Werten der rechten Seite gegenseitig aus, für die Kantenintensität kommt ein Wert nache 0 raus. Sind allerdings die Farbwerte auf der linken Seite des Referenzpixels höher, als die auf der rechten Seite, funktioniert das gegenseitige Auslöschen nicht mehr, und es kommt ein hoher Wert zustande. Umgekehrt entsteht ein negativer Wert, wenn die Farbintensität rechts vom Referenzpixel höher ist, als links.

Die gleiche Matrix kann um 90 Grad gedreht für die Detektion horizontaler Kanten verwendet werden:

```text
|  1  2  1 |
|  0  0  0 |
| -1 -2 -1 |
```

Eine Detektion von beiden Kantenrichtungen findet statt, indem beide Kantendetektionen (horizontal, ```Gx``` und vertikal, ```Gy```) durchgeführt und die beiden Ergebnisse mit der folgenden Formel kombiniert werden: ```G=sqrt(Gx*Gx + Gy*Gy)```.

Ein Beispielbild einer solchen Kantendetektion ist [hier](https://github.com/hkclki/Sobel-Filter) (mit Beispiel-C-Code) zu sehen:

![Beispiel](Bilder/sobel.png)

Bitte beachten Sie ein paar Implementationsdetails:

* Die berechneten Farbwerte können negativ werden - das ergibt keinen Sinn (zumindest nicht für die Darstellung, und wir berechnen hier keine Richtungsinformation), entsprechend sollten Sie für das Kantenbild die Absolutwerte der berechneten Grauwerte verwenden
* Die Kantendetektion wird auf Grauwerten durchgeführt, Sie müssen Pixel von Farbbildern also zunächst in Grauwerte umrechnen. Der empfundene Grauwert eines RGB-Wertes kann nach der Formel: ```Grauwert = 0.2126*Rotwert + 0.7152*Grünwert + 0.0722*Blauwert``` berechnet werden. Hintegrund ist das durch die unterschiedlichen Rezeptoren im menschlichen Auge hervorgerufene Helligkeitsempfinden.  
* Sie werden für die Implementation der Kantendetektion als Ergebnis ein Bild im Farbmodus ```BufferedImage.TYPE_BYTE_GRAY``` verwenden, die dort gespeicherten Pixelwerte können also maximal 255 sein. Das Quellbild wird in der Regel ein volles RGB-Bild mit integer-Werten für die Pixel sein, also können die berechneten Kantenwerte viel größer als 255 werden. Sie sollten sich also bei der Berechnung den maximalen berechneten Farbwert merken und diesen nutzen, um alle Werte auf maximal 255 zu skalieren (also mit dem Skalierungsfaktor ```scale = 255/maxValue``` zu multiplizieren) 

### DICOMImage: Verwaltung eines DICOM-Bildes

Implementieren Sie mit diesem Wissen eine Klasse ```DICOMImage```, die alle Frames eines DICOM-Bildes einliest und verwaltet. Lesen Sie bitte zunächst die Aufgabe bis zum Ende: Es ist sehr empfehlenswert, die Implementation nicht stur in dieser Reihenfolge durchzuführen, sonder stückweise ```DICOMImage``` und ```DICOMFrame``` parallel zu implementieren, so dass man immer testen kann, ob die bisher implementierte Funktionalität auch korrekt ist.

```DICOMImage``` soll folgende Methoden enthalten:
* Einen Constructor ```DICOMImage(File infile, String name)```: Bekommt die DICOM-Datei sowie einen Namen als Argumente. Merkt sich den Namen, und liest alle frames aus ```infile``` in eine interne Liste (am besten eine ```ArrayList``` - wir werden immer wieder über den Index auf die frames zugreifen) von ```DICOMFrame``` ein.
* ```public void writeFrames(int from, int to, boolean original, boolean edges, double edgeLightnessCutoff)```: Speichert die Bilder aus den frames von ```from``` bis ```to``` in Bilddateien. Fall ```original``` ```true``` ist, werden die Originalbilder gespeichert (das Namensschema lautet: <name>_<frame-Nummer>.png). Falls ```edges``` ```true``` ist, werden die Ergebnisse der Kantendetektion mit dem Cutoff ```edgeLightnessCutoff``` ausgegeben (das Namensschema lautet: <name>_<frame-Nummer>_edges.png). Hier empfiehlt es sich sehr, zunächst sowohl in dieser Methode als auch in der ersten Implementation von ```DICOMFrame``` ohne die Kantendetektion zu arbeiten, sondern erst zu überprüfen, ob die eingelesenen frames korrekt als png gespeichert werden.

### DICOMFrame: Ein einzelner frame

Implementieren Sie zudem eine Klasse ```DICOMFrame```, die ein einzelnes frame verwaltet. Sie soll folgende Methoden enthalten:
* Einen Constructor ```public DICOMFrame(BufferedImage image)```: Merkt sich das übergebene BufferedImage.
* ```public BufferedImage getImage()```: Gibt das Bild des frames zurück
* ```public BufferedImage getEdges(double brightness)```: Gibt ein ```BufferedImage``` (im Farbmodus ```BufferedImage.TYPE_BYTE_GRAY```) mit den Ergebnissen der Kantendetektion mit dem Skalierungsfaktor ```brightness``` auf dem Bild des frames aus. Falls noch keine Katendetektion durchgeführt wurde oder die letzte Kantendetektion mit einem anderen Skalierungsfaktor durchgeführt wurde, wird zunächst die Kantendetektion mit dem Skalierungsfaktor ```brightness``` durchgeführt. Sonst wird das Ergebnis der letzten Kantendetektion zurückgegeben (die Kantendetektion dauert einen Moment, sie sollte also nicht unnötig mehrmals durchgeführt werden).

Der Skalierungsfaktor bei der Kantendetektion ist ein Faktor, mit dem die berechneten Kantenwerte multipliziert werden sollen (nachdem die im Theorieteil beschriebene Skalierung auf Werte von 0-255 durchgeführt wurde). Alle Werte, die danach über 255 liegen, werden wieder auf 255 gesetzt. Dadurch wird es ermöglicht, auch schwache Kanten hervorzuheben.

Es bietet sich zudem an, mindestens die folgenden Hilfsmethoden zu verwenden:
* ```private void detectEdges()```: Führt die Kantendetektion mit dem letzten angegebenen Skalierungsfaktor durch (den Sie sich ja in ```getEdges``` sowieso merken müssen) und speichert das Ergebnis in einem ```BufferedImage``` (welches Sie dann in ```getEdges``` zurückgeben können).
* ```private int getGrayscalePixel(BufferedImage image, int x, int y)```: Gibt den Pixel an den Koordinaten ```x``` und ```y``` im Bild ```image``` als Grauwert zurück. Der empfundene Grauwert eines RGB-Wertes kann nach der Formel: ```Grauwert = 0.2126*Rotwert + 0.7152*Grünwert + 0.0722*Blauwert``` berechnet werden. Hintegrund ist das durch die unterschiedlichen Rezeptoren im menschlichen Auge hervorgerufene Helligkeitsempfinden.

### DICOMDiagnostics

Implementieren Sie zuletzt eine Main-Klasse ```DICOMDiagnostics```, die nur die Datei ```data/angiogram1.DCM``` als ein ```DICOMImage``` einliest und von einem frame Ihrer Wahl sowohl das Originalbild als auch das Ergebnis der Kantendetektion mit einer brightness Ihrer Wahl (es sollten aber Kanten darauf erkennbar sein - und zwar von den Blutgefäßen, nicht einfach nur die umlaufende Kante des Bildes) ausgibt.

Da automatische Tests mit nativen Bibliotheken schwer umzusetzen sind, wenn die Entwicklung mit unterschiedlichen Systemen stattfindet, und ich Ihnen wieder lauter Testfehler ersparen möchte, ersetzen Sie bitte wieder einfach die zwei Bilder ```Aufgabe1_frame.png``` sowie ```Aufgabe1_edge.png``` im Verzeichnis ```Bilder``` durch die Ausgaben Ihres Programms. Sie sollten dann hier erscheinen:

Frame:
![Frame](Bilder/Aufgabe1_frame.png)

Edge:
![Edge](Bilder/Aufgabe1_edge.png)