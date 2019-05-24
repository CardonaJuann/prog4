/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grafociudades.controlador;

import grafociudades.excepciones.GrafoExcepcion;
import grafociudades.modelo.Arista;
import grafociudades.modelo.Ficha;
import grafociudades.modelo.GrafoAbstract;
import grafociudades.modelo.GrafoNoDirigido;
import grafociudades.modelo.Vertice;
import grafociudades.utilidad.JsfUtil;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.primefaces.PrimeFaces;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.diagram.ConnectEvent;
import org.primefaces.event.diagram.ConnectionChangeEvent;
import org.primefaces.event.diagram.DisconnectEvent;
import org.primefaces.model.diagram.Connection;
import org.primefaces.model.diagram.DefaultDiagramModel;
import org.primefaces.model.diagram.Element;
import org.primefaces.model.diagram.connector.StraightConnector;
import org.primefaces.model.diagram.endpoint.DotEndPoint;
import org.primefaces.model.diagram.endpoint.EndPoint;
import org.primefaces.model.diagram.endpoint.EndPointAnchor;
import org.primefaces.model.diagram.endpoint.RectangleEndPoint;
import org.primefaces.model.diagram.overlay.LabelOverlay;

/**
 *
 * @author carloaiza
 */
@Named(value = "controladorGrafo")
@SessionScoped
public class ControladorGrafo implements Serializable {

    private GrafoNoDirigido grafoND;
    private DefaultDiagramModel model;
    private Ficha ficha = new Ficha();
    private boolean suspendEvent;
    private List<Vertice> rutaCorta;

    private int codigoInicio = 0;
    private int codigoFinal = 0;

    /**
     * Creates a new instance of ControladorGrafo
     */
    public ControladorGrafo() {
    }

    public Ficha getFicha() {
        return ficha;
    }

    public void setFicha(Ficha ficha) {
        this.ficha = ficha;
    }

    public GrafoNoDirigido getGrafoND() {
        return grafoND;
    }

    public void setGrafoND(GrafoNoDirigido grafoND) {
        this.grafoND = grafoND;
    }

    public DefaultDiagramModel getModel() {
        return model;
    }

    public void setModel(DefaultDiagramModel model) {
        this.model = model;
    }

    public List<Vertice> getRutaCorta() {
        return rutaCorta;
    }

    public void setRutaCorta(List<Vertice> rutaCorta) {
        this.rutaCorta = rutaCorta;
    }

    public int getCodigoInicio() {
        return codigoInicio;
    }

    public void setCodigoInicio(int codigoInicio) {
        this.codigoInicio = codigoInicio;
    }

    public int getCodigoFinal() {
        return codigoFinal;
    }

    public void setCodigoFinal(int codigoFinal) {
        this.codigoFinal = codigoFinal;
    }

    @PostConstruct
    public void inicializar() {

        int numeroFilas = 6;
        int numeroColumnas = 7;
        int numTableros = 3;
        grafoND = new GrafoNoDirigido();

        for (int cont = 1; cont <= (numeroFilas * numeroColumnas * numTableros); cont++) {
            grafoND.adicionarVertice(new Vertice(grafoND.getVertices().size() + 1,
                    new Ficha(cont+"", false)));
        }

        pintarGrafo(grafoND, model, numeroColumnas, numeroFilas);
    }

    private void pintarGrafo(GrafoAbstract grafo, DefaultDiagramModel modelo, int numColumnas, int numFilas) {
        int x = 0;
        int y = 0;
        int numColumActual = 0;
        int xSiguienteFila = 0;
        int numColumDefecto = numColumnas;
        //especificar la separacion entre los vertices
        int separadorDeVertices = 6;
        //especifica en que region empieza a pintar el tablero
        int inicioPintarTalero = 0;
        //contador para pintar las aristas
        int numAristaFila = 1;
        //contador de columnas para aristas
        int contColumAristas = numColumnas;
        //definir las dimenciones de cada tablero
        int dimenciones = numColumnas * numFilas;
        //numero columnas para poner aristas hacia abajo
        int numColumAbajoArista = numColumnas;
        model = new DefaultDiagramModel();
        model.setMaxConnections(-1);

        // model.getDefaultConnectionOverlays().add(new ArrowOverlay(20, 20, 1, 1));
        StraightConnector connector = new StraightConnector();
        connector.setPaintStyle("{strokeStyle:'#404a4e', lineWidth:3}");
        connector.setHoverPaintStyle("{strokeStyle:'#20282b'}");

        model.setDefaultConnector(connector);

        //numero de vertice que se esta pintando
        int numVertice = 1;

        //numero em que separara un tablero del otro
        int separadorTableros = 4;
        for (Vertice vert : grafo.getVertices()) {

            Element element = new Element(vert);
            if (numColumActual >= numColumnas) {

                element.setY(y + separadorDeVertices + "em");
                element.setX(xSiguienteFila + "em");
                xSiguienteFila += separadorDeVertices;

            } else {

                element.setX(x + "em");
                element.setY(y + "em");

            }
            element.setId(String.valueOf(vert.getCodigo()));
            //x = x + 10;

            EndPoint endPointSource = createRectangleEndPoint(EndPointAnchor.RIGHT);
            endPointSource.setSource(true);
            //endPointSource.setTarget(true);
            element.addEndPoint(endPointSource);

            //
            EndPoint endPointSourceLeft = createRectangleEndPoint(EndPointAnchor.LEFT);
            endPointSourceLeft.setSource(true);
            //endPointSource.setTarget(true);
            element.addEndPoint(endPointSourceLeft);

            //
            //
            EndPoint endPointSourceTop = createRectangleEndPoint(EndPointAnchor.TOP);
            endPointSourceTop.setSource(true);
            //endPointSource.setTarget(true);
            element.addEndPoint(endPointSourceTop);

            //
            EndPoint endPointSourceBotoom = createRectangleEndPoint(EndPointAnchor.BOTTOM);
            endPointSourceBotoom.setSource(true);
            //endPointSource.setTarget(true);
            element.addEndPoint(endPointSourceBotoom);

            //posicionar el conector en botton right
            EndPoint endPointSourceBotoomRight = createRectangleEndPointBottomRight(EndPointAnchor.BOTTOM_RIGHT);
            endPointSourceBotoomRight.setSource(true);
            //endPointSource.setTarget(true);
            element.addEndPoint(endPointSourceBotoomRight);

            //posicionar el conector en top left
            EndPoint endPointSourceTopLeft = createRectangleEndPointTopLeft(EndPointAnchor.TOP_LEFT);
            endPointSourceTopLeft.setSource(true);
            //endPointSource.setTarget(true);
            element.addEndPoint(endPointSourceTopLeft);

            //posicionar el conector en top right
            EndPoint endPointSourceTopRight = createRectangleEndPointTopRight(EndPointAnchor.TOP_RIGHT);
            endPointSourceTopRight.setSource(true);
            //endPointSource.setTarget(true);
            element.addEndPoint(endPointSourceTopRight);

            //posicionar el conector en botton left
            EndPoint endPointSourceBottonLeft = createRectangleEndPointBottomLeft(EndPointAnchor.BOTTOM_LEFT);
            endPointSourceBottonLeft.setSource(true);
            //endPointSource.setTarget(true);
            element.addEndPoint(endPointSourceBottonLeft);

            /*
            EndPoint endPoint = createDotEndPoint(EndPointAnchor.LEFT);
            endPoint.setTarget(true);
            element.addEndPoint(endPoint);
            
            EndPoint endPointTop = createDotEndPoint(EndPointAnchor.TOP);
            endPointTop.setTarget(true);
            element.addEndPoint(endPointTop);
             */
            element.setDraggable(false);
            model.addElement(element);
            x += separadorDeVertices;
            numColumActual++;

            //reiniciar las variables para que pinte a partir de la tercera fila
            if (numColumnas == numColumActual) {
                numColumnas = numColumnas;
                numColumActual = 0;
                xSiguienteFila = 0;
                y = y + separadorDeVertices;
                x = inicioPintarTalero;
            }

            //saltar a pintar el proximo tablero
            if (numVertice == (numFilas * numColumDefecto)) {

                x = (separadorDeVertices * numColumDefecto) + separadorTableros;
                y = 0;
                inicioPintarTalero = (separadorDeVertices * numColumDefecto) + separadorTableros;
                numColumDefecto = numColumDefecto * 2;
                //perite cambiar el numero de vertices para pintar los demas tableros
                separadorTableros = separadorTableros * 2;
            }

            //establecer aristas horizontales
            if (numAristaFila == contColumAristas) {

                numAristaFila = 0;
            } else {
                grafoND.getAristas().add(new Arista(numVertice, numVertice + 1, 1));

            }
//establecer aristas verticales
            if ((numVertice + contColumAristas) <= dimenciones) {
                grafoND.getAristas().add(new Arista(numVertice, numVertice + contColumAristas, 1));
            }

            numVertice++;
            numAristaFila++;
        }

        //aristas diagonales derecha
        grafoND.getAristas().add(new Arista(1, 9, 1));
        grafoND.getAristas().add(new Arista(2, 10, 1));
        grafoND.getAristas().add(new Arista(3, 11, 1));
        grafoND.getAristas().add(new Arista(4, 12, 1));
        grafoND.getAristas().add(new Arista(5, 13, 1));
        grafoND.getAristas().add(new Arista(6, 14, 1));
        grafoND.getAristas().add(new Arista(8, 16, 1));
        grafoND.getAristas().add(new Arista(9, 17, 1));
        grafoND.getAristas().add(new Arista(10, 18, 1));
        grafoND.getAristas().add(new Arista(11, 19, 1));
        grafoND.getAristas().add(new Arista(12, 20, 1));
        grafoND.getAristas().add(new Arista(13, 21, 1));
        grafoND.getAristas().add(new Arista(15, 23, 1));
        grafoND.getAristas().add(new Arista(16, 24, 1));
        grafoND.getAristas().add(new Arista(17, 25, 1));
        grafoND.getAristas().add(new Arista(18, 26, 1));
        grafoND.getAristas().add(new Arista(19, 27, 1));
        grafoND.getAristas().add(new Arista(20, 28, 1));
        grafoND.getAristas().add(new Arista(22, 30, 1));
        grafoND.getAristas().add(new Arista(23, 31, 1));
        grafoND.getAristas().add(new Arista(24, 32, 1));
        grafoND.getAristas().add(new Arista(25, 33, 1));
        grafoND.getAristas().add(new Arista(26, 34, 1));
        grafoND.getAristas().add(new Arista(27, 35, 1));
        grafoND.getAristas().add(new Arista(29, 37, 1));
        grafoND.getAristas().add(new Arista(30, 38, 1));
        grafoND.getAristas().add(new Arista(31, 39, 1));
        grafoND.getAristas().add(new Arista(32, 40, 1));
        grafoND.getAristas().add(new Arista(33, 41, 1));
        grafoND.getAristas().add(new Arista(34, 42, 1));

        //aristas diagonales izquierdas
        grafoND.getAristas().add(new Arista(2, 8, 1));
        grafoND.getAristas().add(new Arista(3, 9, 1));
        grafoND.getAristas().add(new Arista(4, 10, 1));
        grafoND.getAristas().add(new Arista(5, 11, 1));
        grafoND.getAristas().add(new Arista(6, 12, 1));
        grafoND.getAristas().add(new Arista(7, 13, 1));
        grafoND.getAristas().add(new Arista(9, 15, 1));
        grafoND.getAristas().add(new Arista(10, 16, 1));
        grafoND.getAristas().add(new Arista(11, 17, 1));
        grafoND.getAristas().add(new Arista(12, 18, 1));
        grafoND.getAristas().add(new Arista(13, 19, 1));
        grafoND.getAristas().add(new Arista(14, 20, 1));
        grafoND.getAristas().add(new Arista(16, 22, 1));
        grafoND.getAristas().add(new Arista(17, 23, 1));
        grafoND.getAristas().add(new Arista(18, 24, 1));
        grafoND.getAristas().add(new Arista(19, 25, 1));
        grafoND.getAristas().add(new Arista(20, 26, 1));
        grafoND.getAristas().add(new Arista(21, 27, 1));
        grafoND.getAristas().add(new Arista(23, 29, 1));
        grafoND.getAristas().add(new Arista(24, 30, 1));
        grafoND.getAristas().add(new Arista(25, 31, 1));
        grafoND.getAristas().add(new Arista(26, 32, 1));
        grafoND.getAristas().add(new Arista(27, 33, 1));
        grafoND.getAristas().add(new Arista(28, 34, 1));
        grafoND.getAristas().add(new Arista(30, 36, 1));
        grafoND.getAristas().add(new Arista(31, 37, 1));
        grafoND.getAristas().add(new Arista(32, 38, 1));
        grafoND.getAristas().add(new Arista(33, 39, 1));
        grafoND.getAristas().add(new Arista(34, 40, 1));
        grafoND.getAristas().add(new Arista(35, 41, 1));

        //Pintar aristas
        int contadorPrueva = 1;
        //segundo contador para detectar salto
        int contSalto = 1;
        //bandera
        boolean bandera = false;

        //ciclo para pintar las aristas
        for (Arista ar : grafoND.getAristas()) {
            //Encuentro origen
            for (Element el : model.getElements()) {
                if (el.getId().compareTo(String.valueOf(ar.getOrigen())) == 0) {
                    for (Element elDes : model.getElements()) {
                        if (elDes.getId().compareTo(String.valueOf(ar.getDestino())) == 0) {

                            if (contadorPrueva == 2 || contadorPrueva == 4 || contadorPrueva == 6 || contadorPrueva == 8 || contadorPrueva == 10 || contadorPrueva == 12 || contadorPrueva == 13 || contadorPrueva == 15 || contadorPrueva == 17 || contadorPrueva == 19 || contadorPrueva == 21 || contadorPrueva == 23 || contadorPrueva == 25 || contadorPrueva == 26 || contadorPrueva == 28 || contadorPrueva == 30 || contadorPrueva == 32 || contadorPrueva == 34 || contadorPrueva == 36 || contadorPrueva == 38 || contadorPrueva == 39 || contadorPrueva == 41 || contadorPrueva == 43 || contadorPrueva == 45 || contadorPrueva == 47 || contadorPrueva == 49 || contadorPrueva == 51 || contadorPrueva == 52 || contadorPrueva == 54 || contadorPrueva == 56 || contadorPrueva == 58 || contadorPrueva == 60 || contadorPrueva == 62 || contadorPrueva == 64 || contadorPrueva == 65) {
                                Connection conn = new Connection(el.getEndPoints().get(3), elDes.getEndPoints().get(2));

                                conn.getOverlays().add(new LabelOverlay(String.valueOf(ar.getPeso()), "flow-label", 0.5));
                                model.connect(conn);

                            } else if (contadorPrueva >= 174) {

                                Connection conn = new Connection(el.getEndPoints().get(7), elDes.getEndPoints().get(6));
                                conn.getOverlays().add(new LabelOverlay(String.valueOf(ar.getPeso()), "flow-label", 3));
                                model.connect(conn);

                            } else if (contadorPrueva >= 144) {

                                Connection conn = new Connection(el.getEndPoints().get(4), elDes.getEndPoints().get(5));
                                conn.getOverlays().add(new LabelOverlay(String.valueOf(ar.getPeso()), "flow-label", 3));
                                model.connect(conn);

                            } else {
                                Connection conn = new Connection(el.getEndPoints().get(0), elDes.getEndPoints().get(1));

                                conn.getOverlays().add(new LabelOverlay(String.valueOf(ar.getPeso()), "flow-label", 0.5));
                                model.connect(conn);
                            }

                            break;
                        }

                    }
                }

            }
            contSalto++;
            contadorPrueva++;

        }

    }

    public void adicionarCiudad() {
        grafoND.adicionarVertice(new Vertice(grafoND.getVertices().size() + 1,
                ficha));

        JsfUtil.addSuccessMessage("Ciudad Adicionada");

        ficha = new Ficha();
        //pintarGrafo(grafoND, model);
    }

    public void limpiarCiudad() {
        ficha = new Ficha();
    }

    private EndPoint createRectangleEndPoint(EndPointAnchor anchor) {
        RectangleEndPoint endPoint = new RectangleEndPoint(anchor);
        endPoint.setScope("ciudad");
        endPoint.setSource(true);
        endPoint.setStyle("{fillStyle:'#98AFC7'}");
        endPoint.setHoverStyle("{fillStyle:'#5C738B'}");
        return endPoint;
    }

    private EndPoint createRectangleEndPointBottomRight(EndPointAnchor anchor) {
        RectangleEndPoint endPoint = new RectangleEndPoint(anchor);
        endPoint.setScope("ciudad");
        endPoint.setSource(true);
        endPoint.setStyle("{fillStyle:'#98AFC7'}");
        endPoint.setStyleClass("botton_right");
        endPoint.setHoverStyle("{fillStyle:'#5C738B'}");

        return endPoint;
    }

    private EndPoint createRectangleEndPointBottomLeft(EndPointAnchor anchor) {
        RectangleEndPoint endPoint = new RectangleEndPoint(anchor);
        endPoint.setScope("ciudad");
        endPoint.setSource(true);
        endPoint.setStyle("{fillStyle:'#98AFC7'}");
        endPoint.setStyleClass("botton_left");
        endPoint.setHoverStyle("{fillStyle:'#5C738B'}");

        return endPoint;
    }

    private EndPoint createRectangleEndPointTopRight(EndPointAnchor anchor) {
        RectangleEndPoint endPoint = new RectangleEndPoint(anchor);
        endPoint.setScope("ciudad");
        endPoint.setSource(true);
        endPoint.setStyle("{fillStyle:'#98AFC7'}");
        endPoint.setStyleClass("top_right");
        endPoint.setHoverStyle("{fillStyle:'#5C738B'}");

        return endPoint;
    }

    private EndPoint createRectangleEndPointTopLeft(EndPointAnchor anchor) {
        RectangleEndPoint endPoint = new RectangleEndPoint(anchor);
        endPoint.setScope("ciudad");
        endPoint.setSource(true);
        endPoint.setStyle("{fillStyle:'#98AFC7'}");
        endPoint.setStyleClass("top_left");
        endPoint.setHoverStyle("{fillStyle:'#5C738B'}");

        return endPoint;
    }

    public void onConnect(ConnectEvent event) {
        if (!suspendEvent) {

            int origen = Integer.parseInt(event.getSourceElement().getId());
            int destino = Integer.parseInt(event.getTargetElement().getId());
            FacesMessage msg = null;
            try {
                grafoND.verificarArista(origen, destino);
                grafoND.adicionarArista(new Arista(origen, destino, 1));
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Conectado",
                        "Desde " + event.getSourceElement().getData() + " hacia " + event.getTargetElement().getData());

            } catch (GrafoExcepcion ex) {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");

            }
            //pintarGrafo(grafoND, model);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            PrimeFaces.current().ajax().update("frmGrafo");
            PrimeFaces.current().ajax().update("frmCiudad");
        } else {
            suspendEvent = false;
        }
    }

    public void onDisconnect(DisconnectEvent event) {

        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Desconectado",
                "Desde " + event.getSourceElement().getData() + " hacia " + event.getTargetElement().getData());

        int origen = Integer.parseInt(event.getSourceElement().getId());
        int destino = Integer.parseInt(event.getTargetElement().getId());
        grafoND.removerArista(origen, destino);
        FacesContext.getCurrentInstance().addMessage(null, msg);

        PrimeFaces.current().ajax().update("frmGrafo");
        PrimeFaces.current().ajax().update("frmCiudad");
    }

    public void onConnectionChange(ConnectionChangeEvent event) {
        int origenAnt = Integer.parseInt(event.getOriginalSourceElement().getId());
        int destinoAnt = Integer.parseInt(event.getOriginalTargetElement().getId());

        int origen = Integer.parseInt(event.getNewSourceElement().getId());
        int destino = Integer.parseInt(event.getNewTargetElement().getId());
        FacesMessage msg = null;
        try {
            grafoND.removerArista(origenAnt, destinoAnt);
            grafoND.verificarArista(origen, destino);
            grafoND.adicionarArista(new Arista(origen, destino, 1));
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Connección Modificada",
                    "Origen inicial: " + event.getOriginalSourceElement().getData()
                    + ", Nuevo Origen: " + event.getNewSourceElement().getData()
                    + ",Destino inicial: " + event.getOriginalTargetElement().getData()
                    + ", Nuevo Destino: " + event.getNewTargetElement().getData());

        } catch (GrafoExcepcion ex) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
            // pintarGrafo(grafoND, model);
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
        PrimeFaces.current().ajax().update("frmGrafo");
        PrimeFaces.current().ajax().update("frmCiudad");
        suspendEvent = true;
    }

    private EndPoint createDotEndPoint(EndPointAnchor anchor) {
        DotEndPoint endPoint = new DotEndPoint(anchor);
        endPoint.setScope("ciudad");
        endPoint.setTarget(true);
        endPoint.setStyle("{fillStyle:'#98AFC7'}");
        endPoint.setHoverStyle("{fillStyle:'#5C738B'}");

        return endPoint;
    }

    public void onRowEdit(RowEditEvent event) {
        Arista ar = ((Arista) event.getObject());

        FacesMessage msg = new FacesMessage("Arista Modificada", ar.toString());
        FacesContext.getCurrentInstance().addMessage(null, msg);
        // pintarGrafo(grafoND, model);
        PrimeFaces.current().ajax().update("frmGrafo");

    }

    public void onRowCancel(RowEditEvent event) {
        Arista ar = ((Arista) event.getObject());
        FacesMessage msg = new FacesMessage("Edición  Cancelada", ar.toString());
        FacesContext.getCurrentInstance().addMessage(null, msg);
        PrimeFaces.current().ajax().update("frmGrafo");

    }

    public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();

        if (newValue != null && !newValue.equals(oldValue)) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cell Changed", "Old: " + oldValue + ", New:" + newValue);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        PrimeFaces.current().ajax().update("frmGrafo");
    }

    public void calcularRutaCorta() {
        if (codigoFinal != codigoInicio) {
            Dijkstra dijstra = new Dijkstra(grafoND,
                    grafoND.obtenerVerticexCodigo(codigoInicio), grafoND.obtenerVerticexCodigo(codigoFinal));

            rutaCorta = dijstra.calcularRutaMasCorta();
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Origen y Destino no pueden ser iguales", "Origen y Destino no pueden ser iguales");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            PrimeFaces.current().ajax().update("grwErrores");
        }
    }
}
