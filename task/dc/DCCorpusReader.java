package task.dc;

import text.*;
import java.util.List;

public abstract class DCCorpusReader {
	abstract public List<Paragraph>[] read_corpus(String x);
}
