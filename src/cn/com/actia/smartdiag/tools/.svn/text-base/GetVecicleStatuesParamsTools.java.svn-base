package cn.com.actia.smartdiag.tools;

import java.util.ArrayList;
import java.util.HashMap;

import cn.com.actia.businesslogic.NumericParameter;
import cn.com.actia.businesslogic.Parameter;

public class GetVecicleStatuesParamsTools {

	public static HashMap<String, Boolean> brokenedParameters;

	public static ArrayList<Parameter> getComparedParameter(int start, int end,
			ArrayList<Parameter> startParameters) {
		// 如果开始的数量没有结束的数量多，则可以直接返回
		if (start >= end) {
			return startParameters;
		}
		ArrayList<Parameter> endParameters = new ArrayList<Parameter>();
		// ----------------------将所有的parameters，名字相同的分组-----------------------//
		// 如果开始的数量与结束的数目要要少
		ArrayList<ArrayList<Parameter>> tempTwoDimensionalparameter = new ArrayList<ArrayList<Parameter>>();
		ArrayList<String> names = new ArrayList<String>();
		for (Parameter p : startParameters) {
			String name = p.getName();
			// 如果在名称数组中已经存在这个名称，则将他们放到同一个arraylist里面去
			if (names.contains(name)) {
				tempTwoDimensionalparameter.get(names.indexOf(name)).add(p);
			}/* 如果不包含,则在名称数组中添加这个名称 */else {
				names.add(name);
				tempTwoDimensionalparameter.add(new ArrayList<Parameter>());
				tempTwoDimensionalparameter.get(names.indexOf(name)).add(p);
				// System.out.println("addname");
			}
		}
		// for (int i = 0; i < names.size(); i++) {
		// String name = names.get(i);
		// System.out.println("名称：" + name + "\t" + "对应的个数:"
		// + tempTwoDimensionalparameter.get(i).size() + "\t实际里面的名称："
		// + tempTwoDimensionalparameter.get(i).get(0).getName());
		// }
		// ------------------分组结束打印每个组的大小，和这个组的对应得名字----------------------//
		for (int i = 0; i < tempTwoDimensionalparameter.size(); i++) {
			endParameters
					.add(choiceOneFromParameterTheSamename(tempTwoDimensionalparameter
							.get(i)));
		}
		return endParameters;
	}

	public static Parameter choiceOneFromParameterTheSamename(
			ArrayList<Parameter> listParameters) {
		// 如果 数组的长度等于零，则可以直接抛出错误了，这个是为了给程序员方便
		if (listParameters.size() == 0) {
			try {
				throw new Exception();
			} catch (Exception e) {
				Utils.showTag("這裏出錯了");
				e.printStackTrace();
				return null;
			}
		}
		if (listParameters.size() == 1) {
			return listParameters.get(0);
		}// 如果数组的长度超过了1 ，这个是要重点处理的
		else {
			// 如果是数值式的就麻烦了
			if (listParameters.get(0) instanceof NumericParameter) {
				int maxsizeEcuList = 0;
				int choicedIndex = 0;
				// 是不是由ECULIST的大小判断的 ，标志
				boolean shoudJudgeFromValue = false;
				for (int i = 0; i < listParameters.size(); i++) {
					NumericParameter parameter = (NumericParameter) listParameters
							.get(i);
					// 如果有最大最小值
					if (parameter.hasMaxValue()) {
						shoudJudgeFromValue = true;
						if (maxsizeEcuList < parameter.getEcuNameList().size()) {
							maxsizeEcuList = parameter.getEcuNameList().size();
							choicedIndex = i;
						}
					} else {// 如果没有最大最小值
						if (maxsizeEcuList < parameter.getEcuNameList().size()) {
							maxsizeEcuList = parameter.getEcuNameList().size();
							choicedIndex = i;
							return listParameters.get(choicedIndex);
						}
					}
				}
				// 如果所有的parameters的Eculist的值的大小都为1；
				if (maxsizeEcuList == 1) {
					if (shoudJudgeFromValue) {/* 如果是需要根据数据的最大值，最小值判断 */
						// 重新开始循环，若真实值不等于最小值或者最大值，则直接使用
						for (int i = 0; i < listParameters.size(); i++) {
							NumericParameter parameter = (NumericParameter) listParameters
									.get(i);
							if (parameter.getParameterValue().getValue() != parameter
									.getMaxValue()
									&& parameter.getParameterValue().getValue() != parameter
											.getMinValue()) {
								choicedIndex = i;
								break;
							}
						}
					} /*
					 * else {//如果不需要通过最大值最小值来判断则，随便取值一个，故可以保持choiedeIndex的值不变
					 * 
					 * }
					 */
				}
				return listParameters.get(choicedIndex);
			}/* 如果不是数值式的就比较好弄 */else {
				int maxsizeEcuList = 0;
				int choicedIndex = 0;
				for (int i = 0; i < listParameters.size(); i++) {
					Parameter parameter = listParameters.get(i);
					if (maxsizeEcuList < parameter.getEcuNameList().size()) {
						maxsizeEcuList = parameter.getEcuNameList().size();
						choicedIndex = i;
					}
				}
				return listParameters.get(choicedIndex);
			}
		}
	}
}
