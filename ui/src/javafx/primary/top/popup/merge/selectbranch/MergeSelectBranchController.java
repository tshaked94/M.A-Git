package javafx.primary.top.popup.merge.selectbranch;

import engine.Branch;
import engine.MergeNodeMaps;
import javafx.StageUtilities;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.primary.top.TopController;
import javafx.primary.top.popup.PopupController;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;

import java.io.IOException;
import java.util.Map;

public class MergeSelectBranchController implements PopupController
{

    @FXML private TopController m_TopController;
    @FXML private ChoiceBox<String> branchNamesChoiceBox;

    @FXML private Button startMergeButton;

    @Override
    public void setTopController(TopController i_TopController) { m_TopController = i_TopController; }

    @FXML void startMergeButtonAction(ActionEvent event) throws IOException
    {
        // getting their branch name
        String theirBranchName = branchNamesChoiceBox.getValue();

        // conflicts
        MergeNodeMaps mergeNodeMapsResult = m_TopController.merge(theirBranchName);

        // solve conflicts if exists
        if (mergeNodeMapsResult.getConflicts().size() > 0)
        {
            m_TopController.showMergeSolveConflictsScene(mergeNodeMapsResult);
        }

        // commit the merge
        m_TopController.showForcedCommitScene(event);

        // add the second parent to commit
        m_TopController.addParentSHAToNewestCommit(theirBranchName);

        // close stage
        StageUtilities.closeOpenSceneByActionEvent(event);
    }

    public void bindBranchesToChoiceBox()
    {
        Map<String, Branch> branches = m_TopController.getBranches();
        addAllBranchesExceptActiveToChoiceBox(branches);
    }

    public void addAllBranchesExceptActiveToChoiceBox(Map<String, Branch> i_Branches)
    {
        Branch activeBranch = m_TopController.getActiveBranch();

        for(Branch branch : i_Branches.values())
        {
            if (!m_TopController.getActiveBranch().equals(branch))
            {
                branchNamesChoiceBox.getItems().add(branch.getName());
            }
        }

        branchNamesChoiceBox.setValue(activeBranch.getName());
    }


}
