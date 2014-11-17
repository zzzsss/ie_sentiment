package training;

import java.io.*;
import java.util.*;

public class LibsvmInterface extends Mach{
	public static int MODE_CLASS=0,MODE_REG=1;
	static Random RAND_N = new Random();
	static int DEF_NAME_LEN = 10;
	//filenames
	static String FNAME_SCALE = ".scale";
	static String FNAME_MODEL = ".model";
	static String FNAME_RANGE = ".range";
	static String FNAME_TRAIN = ".train";
	static String FNAME_TEST = ".test";
	static String FNAME_OUT = ".output";
	//libsvm (on windows)
	static String LIBSVM_SCALE = "./svm/windows/svm-scale.exe";
	static String LIBSVM_TRAIN = "./svm/windows/svm-train.exe";
	static String LIBSVM_TEST = "./svm/windows/svm-predict.exe";
	static String LIBSVM_GRID = "./svm/test/grid.py";
	static int LIBSVM_CV_FOLD = 5;
	static String LIBSVM_OPTION_REG = " -s 3 ";
	
	int mode;
	String svm_name;
	
	//------------
	private void deal_with_name(String name){
		if(name == null){
			StringBuilder s = new StringBuilder();
			for(int i=0;i<DEF_NAME_LEN;i++){
				s.append((char)('a'+RAND_N.nextInt(26)));
			}
			svm_name = s.toString();
		}
		else
			svm_name = name;
	}
	private void write_data(List<Double> v,List<int[]> i,List<double[]> f,String fname){
		try{
			FileOutputStream out = new FileOutputStream(fname);
			PrintStream p = new PrintStream(out);
			for(int ii=0;ii<v.size();ii++){
				int[] t_index = i.get(ii);
				double[] t_value = f.get(ii);
				p.print(v.get(ii));
				p.print(" ");
				for(int jj=0;jj<t_index.length;jj++)
					p.print(t_index[jj]+":"+t_value[jj]+" ");
				p.println();
			}
			p.close();
		}catch (FileNotFoundException e){
            e.printStackTrace();
        }
	}
	//spcified one
	private void write_data(List<int[]>[] index,String fname){
		try{
			FileOutputStream out = new FileOutputStream(fname);
			PrintStream p = new PrintStream(out);
			for(int ii=0;ii<index.length;ii++){
				List<int[]> tl = index[ii];
				for(int[] x : tl){
					p.print(ii+" ");
					for(int xx : x)
						p.print(xx+":1 ");
					p.println();
				}
			}
			p.close();
		}catch (FileNotFoundException e){
            e.printStackTrace();
        }
	}
	private void scale_data(String fname,String sname,String rangename,boolean store){
		try {
            Runtime run = Runtime.getRuntime();
            Process p = null;
            if(store)
            	p = run.exec(LIBSVM_SCALE+" -l 0 -s "+rangename+" "+fname+" > "+sname);
            else
            	p = run.exec(LIBSVM_SCALE+" -l 0 -r "+rangename+" "+fname+" > "+sname);
            p.waitFor();
		}catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
	}
	private String cross_v(String name,String pass_options){
		try {
            Runtime run = Runtime.getRuntime();
            Process p = run.exec("python "+LIBSVM_GRID+" -gnuplot null -v "+LIBSVM_CV_FOLD+" -svmtrain "+LIBSVM_TRAIN+" "+pass_options+" "+name);
            BufferedInputStream in = new BufferedInputStream(p.getInputStream());
            BufferedReader inBr = new BufferedReader(new InputStreamReader(in));
            String lineStr=null,lineStr2=null;
            while (true){
                //System.out.println(lineStr);
                lineStr2 = lineStr;
                lineStr = inBr.readLine();
                if(lineStr==null)
                	break;
            }
            String [] result = lineStr2.split(" ");
            System.out.println("CV for "+svm_name+" "+lineStr2);
            return " -c "+result[0]+" -g "+result[1]+" ";
		}catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
	}
	private void libsvm_train(String fname,String mname,String pass){
		try {
            Runtime run = Runtime.getRuntime();
            Process p = run.exec(LIBSVM_TRAIN+" "+pass+" "+fname+" "+mname);
            p.waitFor();
		}catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
	}
	private void libsvm_test(String fname,String mname,String output){
		try {
            Runtime run = Runtime.getRuntime();
            Process p = run.exec(LIBSVM_TEST+" "+fname+" "+mname+" "+output);
            p.waitFor();
		}catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
	}
	
	//--------------
	public LibsvmInterface(File filename){
		super(1);
		try{
			FileInputStream in = new FileInputStream(filename);
			Scanner sin = new Scanner(in);
			if(sin.next() != NAMES[mach_id]){
				sin.close();
				throw new RuntimeException("Wrong mach file, should be naiveb.");
			}
			svm_name = sin.next();
			mode = sin.nextInt();
			sin.close();
		}catch (FileNotFoundException e){
            e.printStackTrace();
            throw new RuntimeException("File not exist: "+filename);
		}
	}
	
	//specified one --- classes as classification or regression
	public LibsvmInterface(int c,List<int[]>[] index,int m,String svm_n){
		super(1);
		mode = m;
		deal_with_name(svm_n);
		//write training file
		String train_name = svm_name+FNAME_TRAIN;
		String scale_name = train_name+FNAME_SCALE;
		String range_name = svm_name+FNAME_RANGE;
		String model_name = svm_name+FNAME_MODEL;
		String reg_option = (mode==MODE_CLASS)?" ":LIBSVM_OPTION_REG;
		write_data(index,train_name);
		scale_data(train_name,scale_name,range_name,true);
		String pass = cross_v(scale_name,reg_option);
		libsvm_train(scale_name,model_name,pass+reg_option);
	}
	
	public double evaluate(int[]i,double[]f){
		//not support
		throw new RuntimeException("No support for single point");
	}
	public List<Double> evaluate(List<int[]> i,List<double[]> f){
		List<Double> ret = new ArrayList<Double>();
		//write test file
		String test_name = svm_name+FNAME_TEST;
		String scale_name = test_name+FNAME_SCALE;
		String range_name = svm_name+FNAME_RANGE;
		String model_name = svm_name+FNAME_MODEL;
		String output_name = test_name+FNAME_OUT;
		if(f==null){
			//special one
			List<int[]>[] xx = new List[]{i};
			write_data(xx,test_name);
		}
		else{
			//no meaning xx
			List<Double> xx = new ArrayList<Double>(i.size());
			for(int j=0;j<i.size();j++)
				xx.add(0.0);
			write_data(xx,i,f,test_name);
		}
		scale_data(test_name,scale_name,range_name,false);
		libsvm_test(scale_name,model_name,output_name);
		try{
			FileInputStream in = new FileInputStream(output_name);
			Scanner sin = new Scanner(in);
			for(int j=0;j<i.size();j++){
				ret.add(sin.nextDouble());
			}
			sin.close();
		}catch (FileNotFoundException e){
            e.printStackTrace();
            throw new RuntimeException();
		}
		return ret;
	}
	
	public void write(String filename){
		try{
			FileOutputStream out = new FileOutputStream(filename);
			PrintStream p = new PrintStream(out);
			p.println(NAMES[mach_id]);
			p.println(svm_name);
			p.println(mode);
			p.close();
		}catch (FileNotFoundException e){
            e.printStackTrace();
        }
	}
	
	public static void main(String[] x){
	}
}
