package javafx.primary.bottom;

import engine.*;
import javafx.AppController;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BottomController
{
    private AppController m_MainController;
    private Image folderIcon = new Image(getClass().getResourceAsStream("/javafx/primary/bottom/icons/folderIcon.png"));
    private Image blobIcon = new Image(getClass().getResourceAsStream("/javafx/primary/bottom/icons/blobIcon.png"));
    public void setMainController(AppController i_MainController)
    {
        m_MainController = i_MainController;
    }

    @FXML private TabPane bottomTabPane;
    @FXML private Tab commitTab;
    @FXML private Text authorText;
    @FXML private Text dateText;
    @FXML private Text sha1Text;
    @FXML private Text parentText;
    @FXML private Text messageText;
    @FXML private Text containedInBranchesText;
    @FXML private TreeView<String> commitTreeView;
    @FXML private TextArea commitFileTextArea;

    public void setBottomTabsDetails(Commit i_NewValue, String i_CommitSHA1)
    {
        setCommitTabDetails(i_NewValue, i_CommitSHA1);
        setFileTreeTabDetails(i_NewValue);
    }

    private void setFileTreeTabDetails(Commit i_NewValue)
    {
        Folder rootFolder = m_MainController.getFolderBySHA1(i_NewValue.getRootFolderSHA1());
        TreeItem<String> root = new TreeItem<>("Root folder", new ImageView(folderIcon));
        setFileTreeTabDetailsRecursion(rootFolder, root);
        commitTreeView.setRoot(root);
    }

    private void setFileTreeTabDetailsRecursion(Folder i_RootFolder, TreeItem<String> i_RootTreeItem)
    {
        //this method is walking on the folders of the commit and making a treeview from it
        for (Item item : i_RootFolder.getItems())
        {
            if(item.getType().equals("blob"))
            {
                i_RootTreeItem.getChildren().add(new TreeItem<String>(item.getName(), new ImageView(blobIcon)));
            }
            else
            {
                TreeItem<String> folderTreeItem = new TreeItem<>(item.getName(), new ImageView(folderIcon));
                i_RootTreeItem.getChildren().add(folderTreeItem);
                Folder folder = m_MainController.getFolderBySHA1(item.getSHA1());
                setFileTreeTabDetailsRecursion(folder, folderTreeItem);
            }
        }
    }

    private void setCommitTabDetails(Commit i_NewValue, String i_CommitSHA1)
    {
        setAuthorDetails(i_NewValue);

        setDateDetails(i_NewValue);

        setSHA1Details(i_CommitSHA1);

        setParentsDetails(i_NewValue);

        setMessageDetails(i_NewValue);

        setContainingBranchesDetails(i_CommitSHA1);
    }

    private void setContainingBranchesDetails(String i_CommitSHA1)
    {
        List<Branch> containedBranches = getContainedBranches(i_CommitSHA1);
        String containedBranchesString = generateContainedBranchesString(containedBranches);
        containedInBranchesText.setText(containedBranchesString);
    }

    private void setMessageDetails(Commit i_NewValue)
    {
        messageText.setText(i_NewValue.getMessage());
    }

    private void setParentsDetails(Commit i_NewValue)
    {
        String parents = generateParentsString(i_NewValue.getParentsSHA1());
        parentText.setText(parents);
    }

    private void setSHA1Details(String i_CommitSHA1)
    {
        sha1Text.setText(i_CommitSHA1);
    }

    private void setDateDetails(Commit i_NewValue)
    {
        dateText.setText(DateUtils.FormatToString(i_NewValue.getCommitDate()));
    }

    private void setAuthorDetails(Commit i_NewValue)
    {
        authorText.setText(i_NewValue.getCommitAuthor());
    }

    private List<Branch> getContainedBranches(String i_CommitSHA1)
    {
        return m_MainController.getContainedBranches(i_CommitSHA1);
    }

    private String generateContainedBranchesString(List<Branch> i_ContainedBranches)
    {
        String result = "";

        for(Branch branch : i_ContainedBranches)
        {
            result = result.concat(branch.getName() + ", ");
        }

        if(!result.equals(""))
        {
            result = result.substring(0, result.length() - 2);
        }

        return result;
    }

    private String generateParentsString(List<String> i_ParentsSHA1)
    {
        String result = "";

        for(String parent : i_ParentsSHA1)
        {
            result = result.concat(parent + ", ");
        }

        if(!result.equals(""))
        {
            result = result.substring(0, result.length() - 2);
        }

        return result;
    }
}
