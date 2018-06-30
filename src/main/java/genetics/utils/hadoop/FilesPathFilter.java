package genetics.utils.hadoop;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;

public class FilesPathFilter extends Configured implements PathFilter {

    private String prefix;
    private String extension;

    public FilesPathFilter(String prefix, String extension) {
        this.prefix = prefix;
        this.extension = extension;
    }

    @Override
    public boolean accept(Path path) {
        boolean condition = true;

        if (this.prefix != null)
            condition = path.getName().startsWith(this.prefix);

        if (this.extension != null)
            condition &= path.getName().endsWith("." + this.extension);

        return condition;
    }
}
