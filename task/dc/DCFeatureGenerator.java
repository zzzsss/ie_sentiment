package task.dc;

import text.*;
import training.DataPoint;
import java.util.*;

public abstract class DCFeatureGenerator {
	//return Object{List,Dict} --- if d==null then generate one
	abstract public Object[] get_datapoints(List<Paragraph>[] pl,Dict d);
}
