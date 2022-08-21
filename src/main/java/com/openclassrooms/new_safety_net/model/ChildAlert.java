package com.openclassrooms.new_safety_net.model;

import java.util.ArrayList;
import java.util.List;

public class ChildAlert {

	private List<ChildrenOrAdults> listChildren = new ArrayList<>();
	private List<ChildrenOrAdults> listAdult = new ArrayList<>();

	public ChildAlert() {
		super();
	}

	public ChildAlert(List<ChildrenOrAdults> listChildren, List<ChildrenOrAdults> listAdult) {
		super();
		this.listChildren = listChildren;
		this.listAdult = listAdult;
	}

	public List<ChildrenOrAdults> getListChildren() {
		return listChildren;
	}

	public void setListChildren(List<ChildrenOrAdults> listChildren) {
		this.listChildren = listChildren;
	}

	public List<ChildrenOrAdults> getListAdult() {
		return listAdult;
	}

	public void setListAdult(List<ChildrenOrAdults> listAdult) {
		this.listAdult = listAdult;
	}

	@Override
	public String toString() {
		return "ChildAlert [listChildren=" + listChildren + ", listAdult=" + listAdult + "]";
	}

}