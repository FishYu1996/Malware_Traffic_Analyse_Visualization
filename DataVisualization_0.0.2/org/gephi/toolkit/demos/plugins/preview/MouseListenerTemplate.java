
package org.gephi.toolkit.demos.plugins.preview;

import java.awt.Color;
import org.gephi.preview.api.PreviewController;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.NodeIterable;

import javax.swing.JOptionPane;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.Node;
import org.gephi.preview.api.PreviewMouseEvent;
import org.gephi.preview.api.PreviewProperties;
import org.gephi.preview.spi.PreviewMouseListener;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = PreviewMouseListener.class)
public class MouseListenerTemplate implements PreviewMouseListener {

    @Override
    public void mouseClicked(PreviewMouseEvent event, PreviewProperties properties, Workspace workspace) {
        
        //只要点击发生一次（无论是点击空白，还是点击另一个点）就将所有点设为黑色
        for (Node node : Lookup.getDefault().lookup(GraphController.class).getGraphModel(workspace).getGraph().getNodes()) {
            node.setColor(Color.black);
        }
        Lookup.getDefault().lookup(PreviewController.class).refreshPreview(workspace);
        
        //将点击的点和与其相连通的点置为红色
        for (Node node : Lookup.getDefault().lookup(GraphController.class).getGraphModel(workspace).getGraph().getNodes()) {
            if (clickingInNode(node, event)) {

                node.setColor(Color.RED);
                GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
                
                DirectedGraph graph = graphModel.getDirectedGraph();
                NodeIterable it = graph.getNeighbors(node);
                Node[] nodes = it.toArray();
                for(Node n:nodes) {
                    n.setColor(Color.RED);
                }
                
                Lookup.getDefault().lookup(PreviewController.class).refreshPreview(workspace);
                
//                JOptionPane.showMessageDialog(null, "Node " + node.getLabel() + " clicked!");
                event.setConsumed(true);
                return;
            }
        }
        event.setConsumed(true);
    }

    @Override
    public void mousePressed(PreviewMouseEvent event, PreviewProperties properties, Workspace workspace) {
    }

    @Override
    public void mouseDragged(PreviewMouseEvent event, PreviewProperties properties, Workspace workspace) {
    }

    @Override
    public void mouseReleased(PreviewMouseEvent event, PreviewProperties properties, Workspace workspace) {
    }

    private boolean clickingInNode(Node node, PreviewMouseEvent event) {
        float xdiff = node.x() - event.x;
        float ydiff = -node.y() - event.y;
        float radius = node.size();

        return xdiff * xdiff + ydiff * ydiff < radius * radius;
    }
}
