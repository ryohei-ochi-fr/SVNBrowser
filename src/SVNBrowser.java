import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

public class SVNBrowser {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd,HH:mm").withZone(ZoneId.systemDefault());
    private static final String OUTPUT_HEADER = "filename,yyyy/mm/dd,hh:mm,filesize,revision";

    private static void listEntries(SVNRepository repository, String path) throws SVNException {
        Collection<SVNDirEntry> entries = repository.getDir(path, -1, null, (Collection<?>) null);
        for (SVNDirEntry entry : entries) {
            String formattedDate = DATE_FORMAT.format(entry.getDate().toInstant());
            System.out.println(entry.getName() + "," + formattedDate + "," + entry.getSize() + "," + entry.getRevision());

            if (entry.getKind() == SVNNodeKind.DIR) {
                listEntries(repository, (path.isEmpty()) ? entry.getName() : path + "/" + entry.getName());
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Error: Missing arguments. Usage: SVNBrowser <username> <password> <svn:url>");
            System.exit(1);
        }

        String name = args[0];
        String password = args[1];
        String url = args[2];

        try {
            SVNRepository repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
            ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(name, password);
            repository.setAuthenticationManager(authManager);

            System.out.println("Repository Root: " + repository.getRepositoryRoot(true));
            System.out.println(OUTPUT_HEADER);

            listEntries(repository, "");

        } catch (SVNException e) {
            System.err.println("Error while accessing the repository: " + e.getMessage());
        }
    }
}