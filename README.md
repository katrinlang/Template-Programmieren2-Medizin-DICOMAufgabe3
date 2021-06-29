# DICOM-Teilaufgabe 3

In dieser Woche stellen Sie das DICOM-Tool fertig und lernen dabei einige weitere GUI-Komponenten kennen: Das Popup-Menu, welches häufig als Context-Menu bei Rechtsklick eingesetzt wird (```JPopupMenu```) sowie eine Komponente zum Anzeigen großer Inhalte mit Scrollbalken (```JScrollPane```). Auch hier empfiehlt es sich, zunächst die Aufgabenstellung bis zum Ende zu lesen (insbesondere den letzten Teil zur Kommunikation der Komponenten untereinander), bevor Sie mit der Implementation beginnen.

Diese beiden Komponenten setzen Sie ein, um das Hinzufügen und Entfernen von Markierungen auf Bildern zu ermöglichen.

## DICOMFrameMark

Implementieren Sie dafür zunächst eine Klasse ```DICOMFrameMark```, die eine Markierung darstellt. Diese soll die Attribute ```x```, ```y``` und ```size``` (die Größe der Markierung in Pixeln) mit den entsprechenden gettern besitzen. In späteren Versionen könnten weitere Informationen wie ein Notiztext oder die Farbe der Markierung hinzukommen. 

Zudem sollte das ```DICOMFrameMark``` eine Methode ```public boolean isInMark(int x, int y)``` besitzen, die überprüft, ob die übergebenen Koordinaten sich innerhalb der Markierung (also innerhalb der eigenen x- und y-Koordinate +- size) befinden. 

## DICOMFrame

Erweitern Sie dann ```DICOMFrame``` um die folgenden drei Methoden (die Funktionalität sollte selbsterklärend sein):
* ```public void addMark(DICOMFrameMark mark)```
* ```public LinkedList<DICOMFrameMark> getMarks()```
* ```public void removeMark(DICOMFrameMark mark)```

## ImageDetailPanel

Nun ist es an der Zeit, im ```ImageDetailPanel``` das Hinzufügen von Markern zum angezeigten Frame zu ermöglichen.

Fügen Sie dafür dem ```ImageDetailPanel``` einen ```MouseListener``` hinzu, der bei einem Klick mit der linken Maustaste (die Tastennummer bekommen Sie von einem ```MouseEvent``` mit der Methode ```getButton()```, entsprechende Konstanten finden Sie in ```MouseEvent.BUTTON1``` - ```MouseEvent.BUTTON3```) dem aktuell hinzugefügten ```DICOMFrame``` ein ```DICOMFrameMark``` an der Position der Maus (```getX()``` und ```getY()``` in ```MouseEvent```) hinzufügt. Erweitern Sie zudem die ```paintComponent()```-Methode so, dass alle Markierungen des angezeigten ```DICOMFrame``` dargestellt werden.

Erweitern Sie dann den ```MouseListener``` so, dass er bei einem Rechtsklick - falls der Mauszeiger gerade über einem ```DICOMFrameMark``` des aktuell angezeigten ```DICOMFrame``` ist, dafür haben Sie in ```DICOMFrameMark``` die Methode ```isInMark``` implementiert - ein Popup-Menü mit der Option zum Löschen der Markierung anzeigt.

Verwenden Sie dafür ein ```JPopupMenu```. Dieses funktioniert exakt genau so, wie ein ```JMenu```: Sie erstellen es und fügen ihm ```JMenuItem``` mit entsprechenden ```ActionListener```n hinzu. Allerdings fügen Sie es dann nicht einem ```JFrame``` über ```setJMenu``` hinzu, sondern zeigen bei Bedarf es über seite Methode ```show(Component c, int x, int y)``` an. Die als erstes Argument übergebene ```Component c``` ist die GUI-Komponente, zu der die übergebenen x- und y-Koordinaten relativ sind. Überlegen Sie, wie Sie dafür aus dem ```ActionListener``` heraus an das ```ImageDetailPanel``` herankommen - es gibt mehrere Möglichkeiten, eine häufig verwendete Herangehensweise ist eine neue Methode ```getThis()``` zu implementieren, die das Objekt selber (also ```this```) zurückgibt.  

Denken Sie dabei daran, dass das für eine sofortige Aktualisierung der Darstellung ```repaint()``` notwendig sein könnte.

## ImageListPanel

An sich können Sie an dieser Stelle fertig sein. Allerdings ist es recht anstrengend, in einem riesigen Stapel von Frames nach denjenigen zu suchen, in denen Markierungen gesetzt wurden. Eine schönere Lösung ist es, in dem Panel links unter dem Thumbnail der Datei für die Frames, auf denen Markierungen gesetzt wurden, auch entsprechende Thumbnails anzuzeigen und die Frames darüber selektierbar zu machen. 

Da aber ein Thumbnail für das gesamte ```DICOMImage``` etwas andere Funktionalität braucht, als eins für ein einzelnes ```DICOMFrame```, bietet sich hier zunächst eine kleine Refaktorisierung an.

### ImageThumbnail

Erstellen Sie eine neue abstrakte Klasse ```ImageThumbnail```, die von ```JPanel``` ableitet und den Großteil der Funktionalität aus ```SeriesThumbnail``` übernimmt: ```paintComponent``` und ```setSelected``` können Sie komplett übernehmen, den Constructor auch, nur dass hier zur Verallgemeinerung anstatt eines ```DICOMImage``` (aus dem für die Anzeige ja nur der erste ```DICOMFrame``` extrahiert wurde) direkt ein ```DICOMFrame``` übergeben werden sollte. Da dieser übergebene ```DICOMFrame``` in den erbenden Klassen benötigt wird, sollte er in einem ```protected```-Attribut gespeichert werden.

Die Methode ```getDescription``` sollte in ```ImageThumbnail``` abstrakt sein.

### SeriesThumbnail

```SeriesThumbnail``` braucht dann nur noch von ```ImageThumbnail``` ableiten und die Methode ```getDescription``` überschreiben. Zudem sollte der Constructor bestehen bleiben, der ein ```DICOMImage``` bekommt - dieser Constructor muss dann aber nur noch das ```DICOMImage``` intern (für ```getDescription```) speichern und dann den super-Constructor mit dem ersten ```DICOMFrame``` des übergebenen ```DICOMImage``` aufrufen.

An dieser Stelle der Refaktorisierung sollte sich das Programm wieder genau so verhalten, wie davor - die Funktionalität von ```SeriesThumbnail``` hat sich ja nicht verändert, nur dass ein Großteil des Codes nun in ```ImageThumbnail``` steht und nur von ```SeriesThumbnail``` geerbt wird.

### FrameThumbnail

Um ein einzelnes (markiertes) ```DICOMFrame``` im ```ImageListPanel``` anzuzeigen, implementieren Sie nun die Klasse ```FrameThumbnail```, die ebenfalls von ```ImageThumbnail``` ableitet. Auch hier benötigen Sie nicht viel mehr: Im Constructor (der die gleichen Argumente bekommt, wie der von ```ImageThumbnail```) muss nur der super-Constructor aufgerufen und danach das übergebene ```DICOMFrame``` gespeichert werden. Implementieren Sie zudem eine getter-Methode ```public DICOMFrame getFrame()```, die das übergebene Frame zurückgibt. Abschließend müssen Sie noch ```getDescription()``` überschreiben - geben Sie hier einfach den String "Markiert" zurück.

### ImageThumbinal-Erweiterungen

Nun müssen diese Elemente nur noch miteinander verknüpft werden.

Erweitern Sie dafür zunächst die Klasse ```ImageListPanel``` um die folgenden Methoden:

* ```private void updateSize()```: Setzt die preferredSize von dem gesamten Panel auf ```getWidth()```x```getWidth() * (2+Anzahl der angezeigten FrameThumbnail)``` - das wird nachher für das ```JScrollPane``` benötigt.
* ```public void addMarkedFrame(DICOMFrame frame)```: Fügt ein ```FrameThumbnail``` für das übergebene ```DICOMFrame``` dem layout hinzu und setzt die Größe mittels ```updateSize()``` neu. Dieses ```FrameThumbnail``` sollte einen ```MouseListener``` bekommen, der bei Rechtsklick ein ```JPopupMenu``` mit den folgenden Einträgen anzeigt: 
    * Entfernen: Entfernt alle Markierungen von dem ```DICOMFrame``` und entfernt das ```FrameThumbnail``` aus dem ```ImageListPanel```  und setzt die Größe mittels ```updateSize()``` neu (Sie können dafür die Methode ```remove(Component c)``` von ```JPanel``` verwenden, die die übergebene Komponente aus dem Layout entfernt)
    * Speichern: Öffnet einen ```JFileChooser```, mit dem eine neue jpg-Datei ausgewählt werden kann (verwenden Sie dafür statt der beim Laden einer existierenden Datei genutzten Methode ```showOpenDialog``` die Methode ```showSaveDialog``` zum Auswählen einer neuen Datei) und speichert das ```BufferedImage``` des ```DICOMFrame``` in dieser Datei
* ```public void removeMarkedFrame(DICOMFrame frame)```: Entfernt das ```FrameThumbnail```, welches den übergebenen ```DICOMFrame``` anzeigt, aus dem Layout und setzt die Größe mittels ```updateSize()``` neu
* ```public void setSelectedFrame(DICOMFrame frame)```: Setzt von allen angezeigten ```FrameThumbnail```s selected auf ```false```, außer bei dem ```FrameThumbnail```, welches den übergebenen ```DICOMFrame``` anzeigt - dort soll selected auf ```true``` gesetzt werden (wodurch in ```paintComponent``` in diesem ```FrameThumbnail``` ein Rahmen gezeichnet wird).

## MainFrame

### Kommunikation

Implementieren Sie für die Kommunikation der Komponenten untereinander die folgenden Methoden in ```MainFrame```:

* ```public void addMarkedFrame(DICOMFrame frame)```: Fügt dem ```ImageListPanel``` mittels ```addMarkedFrame``` das übergebene ```DICOMFrame``` hinzu. An dieser Stelle wird ein ```revalidate()``` notwendig.
* ```public void removeMarkedFrame(DICOMFrame frame)```: Entfernt aus dem ```ImageListPanel``` mittels ```removeMarkedFrame``` das übergebene ```DICOMFrame```. An dieser Stelle wird ein ```revalidate()``` notwendig.

### Layout

Damit das komplette ```ImageListPanel``` angezeigt werden kann, auch wenn es durch viele ```FrameThumbnail```s sehr lang wird, kann es in ein ```JScrollPane``` (siehe auch die [Oracle-Dokumentation](https://docs.oracle.com/javase/tutorial/uiswing/components/scrollpane.html)) verpackt werden. Im Grunde muss dafür nichts weiter getan werden, als dass ein neues ```JScrollPane``` erstellt wird, dem im Constructor das anzuzeigende ```ImageListPanel``` übergeben wird. Dann wird dem Layout von ```MainFrame``` dieses ```JScrollPane``` hinzugefügt (und nicht mehr das ```ImageListPanel``` direkt).

Damit nicht erst wenn das ```ImageListPanel``` zu groß wird Scrollbalken auftauchen und das Layout durcheinander bringen, kann der horizontale Scrollbalken permanent aus- und der vertikale Scrollbalken permanent eingeschaltet werden, indem die beiden folgenden Policies übergeben werden:

```java
//...
myJScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
myJScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
//...
```

# Kommunikation zwischen den Komponenten

Das Zusammenspiel zwischen den Komponenten und Methoden ist nun wie folgt:

* Wird einem ```DICOMFrame``` durch einen Klick im ```ImageDetailPanel``` die erste Markierung hinzugefügt, ruft dieses im ```MainFrame``` die Methode ```addMarkedFrame``` auf, welche wiederum die ```addMarkedFrame```-Methode in ```ImageListPanel``` aufruft -> es erscheint ein neues ```FrameThumbnail``` in der Liste.
* Wird von einem ```DICOMFrame``` durch Rechtsklick->Entfernen im ```ImageDetailPanel``` die letzte Markierung gelöscht, ruft dieses im ```MainFrame``` die Methode ```removeMarkedFrame``` auf, welche wiederum die ```removeMarkedFrame```-Methode in ```ImageListPanel``` aufruft -> das entsprechende ```FrameThumbnail``` verschwindet aus der Leiste.
* Wird im ```ImageListPanel``` durch Rechtsklick->Entfernen ein ```FrameThumbnail``` entfernt, tut das der Konsistenz halber das ```ImageListPanel``` nicht direkt - es ruft im ```MainFrame``` die Methode ```removeMarkedFrame``` mit dem entsprechenden ```DICOMFrame``` auf.
