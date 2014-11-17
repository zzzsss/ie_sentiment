package nlpir;

import java.io.UnsupportedEncodingException;

import com.sun.jna.Library;
import com.sun.jna.Native;

public class NLPIR {
	// ����ӿ�CLibrary���̳���com.sun.jna.Library
	public interface CLibrary extends Library {
		// ���岢��ʼ���ӿڵľ�̬����
		CLibrary Instance = (CLibrary) Native.loadLibrary(
				"lib/NLPIR", CLibrary.class);
		public int NLPIR_Init(String sDataPath, int encoding,
				String sLicenceCode);
		public String NLPIR_ParagraphProcess(String sSrc, int bPOSTagged);
		public String NLPIR_GetKeyWords(String sLine, int nMaxKeyLimit,
				boolean bWeightOut);
		public String NLPIR_GetFileKeyWords(String sLine, int nMaxKeyLimit,
				boolean bWeightOut);
		public int NLPIR_AddUserWord(String sWord);//add by qp 2008.11.10
		public int NLPIR_DelUsrWord(String sWord);//add by qp 2008.11.10
		public String NLPIR_GetLastErrorMsg();
		public void NLPIR_Exit();
	}

	public static String transString(String aidString, String ori_encoding,
			String new_encoding) {
		try {
			return new String(aidString.getBytes(ori_encoding), new_encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) throws Exception {
		String argu = "lib";
		// String system_charset = "GBK";//GBK----0
		//String system_charset = "UTF-8";
		int charset_type = 1;
		int init_flag = CLibrary.Instance.NLPIR_Init(argu, charset_type, "0");
		String nativeBytes = null;
		if (0 == init_flag) {
			nativeBytes = CLibrary.Instance.NLPIR_GetLastErrorMsg();
			System.err.println("��ʼ��ʧ�ܣ�fail reason is "+nativeBytes);
			return;
		}
		String sInput = "������������ȫ����֯�������������ֺ���ʧ���»��籱�������£����յ�������������������ϯ����ɣ������ڱ������ܼ��߲ɷ�ʱ����˵�����ز��ֵ��������ش�ѩ�ֺ󣬵����롢����Ժʮ�ֹ������ص�����;��ֹ�����ָʾȫ�����þ��ֹ�����";
		//String nativeBytes = null;
		try {
			nativeBytes = CLibrary.Instance.NLPIR_ParagraphProcess(sInput, 1);
			System.out.println("�ִʽ��Ϊ�� " + nativeBytes);
			CLibrary.Instance.NLPIR_AddUserWord("������������");
			nativeBytes = CLibrary.Instance.NLPIR_ParagraphProcess(sInput, 1);
			System.out.println("�����û��ʵ��ִʽ��Ϊ�� " + nativeBytes);
			CLibrary.Instance.NLPIR_DelUsrWord("Ҫ��������ǿ����");
			nativeBytes = CLibrary.Instance.NLPIR_ParagraphProcess(sInput, 1);
			System.out.println("ɾ���û��ʵ��ִʽ��Ϊ�� " + nativeBytes);
			/*
			int nCountKey = 0;
			String nativeByte = CLibrary.Instance.NLPIR_GetKeyWords(sInput, 10,false);
			System.out.print("�ؼ�����ȡ����ǣ�" + nativeByte);
			nativeByte = CLibrary.Instance.NLPIR_GetFileKeyWords("D:\\NLPIR\\feedback\\huawei\\5341\\5341\\�����㳡\\2012\\5\\16766.txt", 10,false);
			System.out.print("�ؼ�����ȡ����ǣ�" + nativeByte);
			*/
			CLibrary.Instance.NLPIR_Exit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
