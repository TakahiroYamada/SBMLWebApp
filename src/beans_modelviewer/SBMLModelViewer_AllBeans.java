package beans_modelviewer;

import java.util.LinkedList;

import beans.superclass_beans.SuperClass_Beans;

public class SBMLModelViewer_AllBeans extends SuperClass_Beans{
	private LinkedList< SBMLModelViewer_ComponentBeans > all;
	public SBMLModelViewer_AllBeans() {
		// TODO Auto-generated constructor stub
		all = new LinkedList< SBMLModelViewer_ComponentBeans >();
	}
	public LinkedList<SBMLModelViewer_ComponentBeans> getAll() {
		return all;
	}
	public void setAll(LinkedList<SBMLModelViewer_ComponentBeans> all) {
		this.all = all;
	}
}
