package javafx.primary.left.committree.node.commit;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.primary.left.committree.node.commit.contextmenu.ContextMenuController;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.net.URL;

import static javafx.CommonResourcesPaths.CONTEXT_MENU_FXML_RESOURCE;

public class CommitNodeController
{
    @FXML private Circle CommitCircle;
    private CommitNode m_CommitNode;


    @FXML
    void commitNodeMouseClicked(MouseEvent event)
    {
        if (event.getButton().equals(MouseButton.PRIMARY))
        {
            // select commit on table view
            String commitSHA1 = m_CommitNode.getSHA1();
            m_CommitNode.commitNodeTreeSelected(commitSHA1);

            // redraw commit tree
            m_CommitNode.updateCommitTree();

            // bold commits hierarchy
            m_CommitNode.boldCommitHierarchy();
        }
    }

    public void setCircleRadius(double x) { CommitCircle.setRadius(x); }

    public Circle getCommitCircle() { return CommitCircle; }

    public int getCircleRadius() {
        return (int)CommitCircle.getRadius();
    }

    public CommitNode getCommitNode()
    {
        return m_CommitNode;
    }

    public void setCommitNode(CommitNode i_CommitNode)
    {
        this.m_CommitNode = i_CommitNode;
    }

    @FXML
    void contextMenuAction(ContextMenuEvent event) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource(CONTEXT_MENU_FXML_RESOURCE);
        fxmlLoader.setLocation(url);
        ContextMenu root = fxmlLoader.load(url.openStream());
        ContextMenuController controller = fxmlLoader.getController();
        controller.setCommitTreeManager(m_CommitNode.getCommitTreeManager());
        controller.setCommitSHA1(m_CommitNode.getSHA1());
        controller.setTopController(m_CommitNode.getCommitTreeManager().getLeftController().getMainController().getTopComponentController());

        // --------- add your shit to this checkbox here ---------
        // send your logic to add branches to checkbox in this method
        controller.addToCheckBox();
        // --------- add your shit to this checkbox here ---------

        root.show(CommitCircle, event.getScreenX(), event.getScreenY());
    }
}
