package org.neuro4j.web.console.controller.view;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.neuro4j.core.Entity;
import org.neuro4j.core.Network;
import org.neuro4j.core.Relation;
import org.neuro4j.core.rel.DirectionRelation;



public class ViewJSPlumbProcessor {
	
	private Network network;
	
	private ProcessorContext context = new ProcessorContext();
	
	public ViewJSPlumbProcessor(Network net)
	{
		this.network = net;
	}


	public LinkedHashSet<ViewComponent> process(String startNodeId) {

		Entity start = null;
		if (startNodeId != null)
		{
			start = network.getEntityByUUID(startNodeId);
		} 
		if (start == null){
			start = network.getEntityByName("Start");
		}


		LinkedHashSet<ViewComponent> viewComponents = new LinkedHashSet<ViewComponent>();

		context.setTop(100);

		ViewComponent component = new ViewComponent(start);
		viewComponents.add(component);
		
		component.setTop(context.getTop());
		
		processViewRelations(component, start, viewComponents);

		return viewComponents;
	}

	
	private  void processViewRelations(ViewComponent component, Entity currentStep, Set<ViewComponent> viewComponents)
	{
		List<Entity> nextEntities = new ArrayList<Entity>();
		
		Entity next = null;
		for (Relation r : currentStep.getRelations())
		{
			if (r.getName().equals("belong to"))
			{
				continue;
			}
			if (null != r)
			{
				String nextEid = r.getProperty(DirectionRelation.TO_KEY);
				if (null == nextEid) 
					continue;

				Set<Entity> rparts = r.getAllParticipants(currentStep.getUuid());
				if (rparts.size() > 0)
				{
					Entity rp = rparts.iterator().next();
					if (nextEid.equals(rp.getUuid()))
					{
						next = rp;
						nextEntities.add(next);	

						component.addConnectionTo(new ViewRelation(currentStep, next));
						
						ViewComponent nextComponent = new ViewComponent(next);
						
						nextComponent.setLeft(component.getLeft() + context.getLeftStep() * (component.getConnectionCount() -1));

						if (!viewComponents.contains(nextComponent))
						{
							nextComponent.setTop(component.getTop() + 160);
							viewComponents.add(nextComponent);
						} else {
							continue;
						}
						
						processViewRelations(nextComponent, next, viewComponents);
					//	break;
					}

			} 
		} // for (Relation r : currentStep.getRelations(NEXT_RELATION_NAME))

	}
}

//private  void processViewRelations(ViewComponent component, Entity currentStep,Set<ViewComponent> viewComponents)
//{
//	List<Entity> nextEntities = new ArrayList<Entity>();
//	
//	Entity next = null;
//	for (Relation r : currentStep.getRelations())
//	{
//		if (r.getName().equals("belong to"))
//		{
//			continue;
//		}
//		if (null != r)
//		{
//			String nextEid = r.getProperty(DirectionRelation.TO_KEY);
//			if (null == nextEid) 
//				continue;
//
//			Set<Entity> rparts = r.getAllParticipants(currentStep.getUuid());
//			if (rparts.size() > 0)
//			{
//				Entity rp = rparts.iterator().next();
//				if (nextEid.equals(rp.getUuid()))
//				{
//					next = rp;
//					nextEntities.add(next);	
////					if (component.getConnectionCount())
////					{
////						
////					}
//					component.addConnectionTo(new ViewRelation(currentStep.getUuid(), next.getUuid()));
//					
//					ViewComponent nextComponent = new ViewComponent(next);
//					
//					nextComponent.setLeft(component.getLeft() + context.getLeftStep() * (component.getConnectionCount() -1));
//
//					if (!viewComponents.contains(nextComponent))
//					{
//						nextComponent.setTop(component.getTop() + 120);
//						viewComponents.add(nextComponent);
//					}
//					
//					processViewRelations(nextComponent, next, viewComponents);
//				//	break;
//				}
//			} 
//		} 
//	} // for (Relation r : currentStep.getRelations(NEXT_RELATION_NAME))
//
//}
}

