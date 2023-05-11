package es.teis.dataXML;

import es.teis.data.exceptions.LecturaException;
import es.teis.model.Partido;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;

public class DOMXMLService implements IXMLService {
    public static ArrayList<Partido> partidosUmbral = new ArrayList<>();
    public static Partido partido;

    @Override
    public ArrayList<Partido> leerPartidos(String ruta, float umbral) throws LecturaException {

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(ruta);
            NodeList list = document.getElementsByTagName(RESULTADOS).item(0).getChildNodes();
            switchElements(list, umbral);

        } catch (Exception e) {
            throw new LecturaException(e.getMessage(),ruta);
        }


        return partidosUmbral;
    }

    private void switchElements(NodeList list, float umbral) {
        long id = 0;
        String nombre = "";
        int votos = 0;
        float porcentaje = 0;
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if(node.getNodeType()==Node.ELEMENT_NODE) {
                switch (node.getNodeName()) {
                    case PARTIDO_TAG:
                        partido = new Partido(id,nombre,votos,porcentaje);
                        id=Long.parseLong(node.getAttributes().getNamedItem(PARTIDO_ATT_ID).getNodeValue());
                        partido.setId(id);
                        NodeList hijo = node.getChildNodes();
                        switchElements(hijo, umbral);
                        break;
                    case PARTIDO_NOMBRE_TAG:
                        nombre=node.getTextContent();
                        partido.setNombre(nombre);
                        break;
                    case PARTIDO_VOTOS_NUM_TAG:
                        votos=Integer.parseInt(node.getTextContent());
                        partido.setVotos(votos);
                        break;
                    case PARTIDO_VOTOS_PORC_TAG:
                        porcentaje=Float.parseFloat(node.getTextContent());
                        partido.setPorcentaje(porcentaje);
                        break;


                }
                if (porcentaje > umbral) {
                    partidosUmbral.add(partido);
                }
            }

        }

    }
}