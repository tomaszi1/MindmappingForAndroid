package edu.agh.idziak;

import org.xmind.core.Core;
import org.xmind.core.CoreException;
import org.xmind.core.IRevision;
import org.xmind.core.IRevisionManager;
import org.xmind.core.IRevisionRepository;
import org.xmind.core.ISheet;
import org.xmind.core.IWorkbook;
import org.xmind.core.IWorkbookBuilder;
import org.xmind.core.io.ByteArrayStorage;
import org.xmind.core.io.IStorage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.agh.cloudtest.dropbox.Utils;

public class WorkbookHandler {
    private IWorkbook workbook;
    private IStorage tempStorage;
    private Map<String, Long> initModTime = new HashMap<String, Long>();

    private WorkbookHandler() {
    }

    public static WorkbookHandler createNewWorkbook() {
        WorkbookHandler wm = new WorkbookHandler();
        IWorkbookBuilder builder = Core.getWorkbookBuilder();
        wm.workbook = builder.createWorkbook();
        wm.tempStorage = new ByteArrayStorage();
        wm.workbook.setTempStorage(wm.tempStorage);
        return wm;
    }

    public static WorkbookHandler loadWorkbookFromStream(InputStream is) throws IOException, CoreException {
        IWorkbookBuilder builder = Core.getWorkbookBuilder();
        WorkbookHandler wm = new WorkbookHandler();
        wm.tempStorage = new ByteArrayStorage();
        wm.workbook = builder.loadFromStream(is, wm.tempStorage);
        for (ISheet s : wm.workbook.getSheets()) {
            wm.initModTime.put(s.getId(), s.getModifiedTime());
        }
        return wm;
    }

    public IWorkbook getWorkbook() {
        return workbook;
    }

    public void saveWorkbookToStream(OutputStream os) throws IOException, CoreException {
        IRevisionRepository revRep = workbook.getRevisionRepository();
        for (ISheet sheet : workbook.getSheets()) {
            Long modTime = initModTime.get(sheet.getId());
            if(modTime == null || !modTime.equals(sheet.getModifiedTime())){
                IRevisionManager revMan = revRep.getRevisionManager(sheet.getId(), IRevision.SHEET);
                revMan.addRevision(sheet);
            }
        }
        workbook.save(os);
        Utils.closeQuietly(os);
    }

    public List<IRevision> getRevisionHistory(ISheet sheet) {
        if(!sheet.getOwnedWorkbook().equals(workbook))
            throw new IllegalArgumentException("Sheet does not belong to this workbook");
        return workbook.getRevisionRepository().getRevisionManager(sheet.getId(),IRevision.SHEET).getRevisions();
    }

    public void revertToRevision(IRevision revision){
        if(revision==null)
            throw new IllegalArgumentException("Revision was null");
        if(!revision.getOwnedWorkbook().equals(workbook))
            throw new IllegalArgumentException("Revision does not belong to this workbook");
        ISheet sheet = (ISheet) workbook.getElementById(revision.getResourceId());
        workbook.removeSheet(sheet);
        ISheet revSheet = (ISheet) revision.getContent();
        workbook.addSheet((ISheet) workbook.importElement(revSheet)); // FIXME: preserve position of sheet
    }


}
