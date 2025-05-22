
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;

public class SVNBrowser {
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd,HH:mm");

    private static void listEntries(SVNRepository repository, String path) throws SVNException {
        Collection entries = repository.getDir(path, -1, null, (Collection) null);
        Iterator iterator = entries.iterator();
        
        
        while (iterator.hasNext()) {
        	
        	// ファイル名
        	// コミット時のタイムスタンプ　年月日と時刻に分かれる
        	// ファイルサイズ(バイト)
        	// リビジョン
        	
            SVNDirEntry entry = (SVNDirEntry) iterator.next();
            
            String formattedDate = DATE_FORMAT.format(entry.getDate());
            System.out.println(entry.getName() + "," + formattedDate + "," + entry.getRevision());

            if (entry.getKind() == SVNNodeKind.DIR) {
                listEntries(repository, (path.equals("")) ? entry.getName() : path + "/" + entry.getName());
            }
        }
    }

    public static void main(String[] args) {
//        String url = "svn://localhost/repos/enecom/branches/HN_S20250601_03_NWKANRI_SCOPE2_ST/BATCH/src/main/java/jp/co/energia/nw/batch/NW1R";
//        String name = "ryohei.ochi";
//        String password = "zHjWGW6V";
        if (args.length != 3) {
            System.out.println("usage: list4svn username password svn:url");
            System.exit(1);
        }

        String name = args[0];
        String password = args[1];
        String url = args[2];
        
        System.out.println("filename,yyyy/mm/dd,hh:mm,filesize,revision");
        
        try {
            SVNRepository repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
            ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(name, password);
            repository.setAuthenticationManager(authManager);

            System.out.println("Repository Root: " + repository.getRepositoryRoot(true));
            // getUUID() の呼び出しを削除

            listEntries(repository, "");

        } catch (SVNException e) {
            System.err.println("Error while accessing the repository: " + e.getMessage());
            e.printStackTrace();
        }
    }
}