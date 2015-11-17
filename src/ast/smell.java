package ast;

import java.util.HashSet;
import java.util.LinkedList;

import constant.Constant;
import parse.Tokenizer;
import simulate.Critter;
import simulate.Position;
import simulate.World;

public class smell extends Expr{

	class DetailedPosition{
		Position pos;
		int direction;
		int initialDirection;
		int distance;
		
		DetailedPosition(Position p, int dir, int ini, int dis) {
			pos = p;
			direction = dir;
			initialDirection = ini;
			distance = dis;
		}
		
		public int hashCode() {
			return 313 * direction + pos.hashCode();
		}
		
		public boolean equals(Object o) {
			if(o instanceof DetailedPosition) {
				DetailedPosition that = (DetailedPosition)o;
				if(this.pos.equals(that.pos) && this.direction == that.direction)
					return true;
			}
			return false;
		}
		
		public String toString() {
			return ""+ pos.toString() + ":" + direction + ":" + distance;
		}
	}
	
	@Override
	public int size() {
		return 1;
	}

	@Override
	public Node nodeAt(int index) {
		if(index == 0)
			return this;
		return null;
	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		return sb.append("smell");
	}

	@Override
	public MutableNode parseMyType(Tokenizer t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String eval(Critter c, World w) {
		DetailedPosition start = new DetailedPosition(c.getPosition(), c.getDir(), 0, 0);
		DetailedPosition p = BFS(w, start);
		if(p == null)
			return "1000000";
		return "" + (p.distance*1000 + p.initialDirection);
	}

	@Override
	public boolean beMutated(AbstractMutation m) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Expr copy() {
		return new smell();
	}

	public DetailedPosition BFS(World world, DetailedPosition start) {
		HashSet<DetailedPosition> visited = new HashSet<>();
		visited.add(start);
		LinkedList<DetailedPosition> queue = new LinkedList<>();
		DetailedPosition left = new DetailedPosition(start.pos,
												(start.direction+1)%6,
												(start.direction+1)%6,1);
		DetailedPosition right = new DetailedPosition(start.pos,
												  (start.direction+5)%6,
												  (start.direction+5)%6,1);
		queue.add(left);
		queue.add(right);
		
		Position ahead = start.pos.getNextStep(start.direction);
		DetailedPosition front = new DetailedPosition(ahead,
													start.direction,
													start.direction, 1);
		if(world.getElemAtPosition(ahead) == null 
				|| world.getElemAtPosition(ahead).getType().equals("FOOD"))
			queue.add(front);
		
		Position back = start.pos.getNextStep((start.direction+3)%6);
		DetailedPosition backward = new DetailedPosition(back,
														start.direction, 
														start.direction,1);
		if(world.getElemAtPosition(back) == null)
			queue.add(backward);
		while(!queue.isEmpty()) {
			DetailedPosition temp = queue.removeFirst();
			if(temp.distance > Constant.MAX_SMELL_DISTANCE)
				break;
			if(world.getElemAtPosition(temp.pos) != null
					&& world.getElemAtPosition(temp.pos).getType().equals("FOOD"))
				return temp;
			visited.add(temp);
			left = new DetailedPosition(temp.pos, 
										(temp.direction+1)%6,
										temp.initialDirection,
										temp.distance+1);
			if(!visited.contains(left))
				queue.add(left);
			right = new DetailedPosition(temp.pos, 
										(temp.direction+5)%6,
										temp.initialDirection,
										temp.distance+1);
			if(!visited.contains(right))
				queue.add(right);
			ahead = temp.pos.getNextStep(temp.direction);
			front = new DetailedPosition(ahead,
										temp.direction,
										temp.initialDirection,
										temp.distance+1);
			if(world.getElemAtPosition(ahead) == null
					|| world.getElemAtPosition(ahead).getType().equals("FOOD")
					&& !visited.contains(front))
				queue.add(front);
			back = temp.pos.getNextStep((temp.direction+3)%6);
			backward = new DetailedPosition(back,
											temp.direction,
											temp.initialDirection,
											temp.distance+1);
			if(world.getElemAtPosition(back) == null
				&& !visited.contains(back))
				queue.add(backward);
		}
		return null;
	}
	
}
