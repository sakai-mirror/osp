package org.theospi.portfolio.tagging.impl;

import java.util.HashSet;
import java.util.Set;

import org.sakaiproject.taggable.api.TaggableItem;
import org.theospi.portfolio.tagging.api.DecoratedTaggableItem;

public class DecoratedTaggableItemImpl implements DecoratedTaggableItem {
	private String typeName;
	private Set<TaggableItem> taggableItems = new HashSet<TaggableItem>();
	
	public DecoratedTaggableItemImpl() {
	}
	
	public DecoratedTaggableItemImpl(String typeName) {
		this.typeName = typeName;
	}
	
	public DecoratedTaggableItemImpl(String typeName, Set<TaggableItem> taggableItems) {
		this.typeName = typeName;
		this.taggableItems = taggableItems;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public Set<TaggableItem> getTaggableItems() {
		return taggableItems;
	}

	public void setTaggableItems(Set<TaggableItem> taggableItems) {
		this.taggableItems = taggableItems;
	}
	
	public void addTaggableItem(TaggableItem taggableItem) {
		this.taggableItems.add(taggableItem);
	}
}
