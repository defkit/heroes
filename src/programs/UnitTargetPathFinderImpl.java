package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.Edge;
import com.battle.heroes.army.programs.UnitTargetPathFinder;

import java.util.*;

public class UnitTargetPathFinderImpl implements UnitTargetPathFinder {
    
    private static final int WIDTH = 27;
    private static final int HEIGHT = 21;
    
    // Направления движения (включая диагонали)
    private static final int[] DX = {-1, -1, -1, 0, 0, 1, 1, 1};
    private static final int[] DY = {-1, 0, 1, -1, 1, -1, 0, 1};
    
    // Класс для хранения информации о клетке в BFS
    private static class Cell {
        int x, y;
        Cell parent;
        
        Cell(int x, int y, Cell parent) {
            this.x = x;
            this.y = y;
            this.parent = parent;
        }
    }
    
    @Override
    public List<Edge> getTargetPath(Unit attackUnit, Unit targetUnit, List<Unit> existingUnitList) {
        if (attackUnit == null || targetUnit == null) {
            return new ArrayList<>();
        }
        
        int startX = attackUnit.getxCoordinate();
        int startY = attackUnit.getyCoordinate();
        int targetX = targetUnit.getxCoordinate();
        int targetY = targetUnit.getyCoordinate();
        
        // Проверяем границы
        if (!isValid(startX, startY) || !isValid(targetX, targetY)) {
            return new ArrayList<>();
        }
        
        // Создаем карту занятых клеток
        // Сложность: O(N), где N - количество существующих юнитов
        Set<String> occupied = new HashSet<>();
        for (Unit unit : existingUnitList) {
            if (unit != null && unit != attackUnit && unit != targetUnit && unit.isAlive()) {
                int x = unit.getxCoordinate();
                int y = unit.getyCoordinate();
                if (isValid(x, y)) {
                    occupied.add(x + "," + y);
                }
            }
        }
        
        // BFS для поиска кратчайшего пути
        // Сложность: O(WIDTH * HEIGHT)
        Queue<Cell> queue = new LinkedList<>();
        boolean[][] visited = new boolean[WIDTH][HEIGHT];
        Cell[][] parent = new Cell[WIDTH][HEIGHT];
        
        Cell start = new Cell(startX, startY, null);
        queue.offer(start);
        visited[startX][startY] = true;
        parent[startX][startY] = null;
        
        Cell targetCell = null;
        
        while (!queue.isEmpty()) {
            Cell current = queue.poll();
            
            // Если достигли цели
            if (current.x == targetX && current.y == targetY) {
                targetCell = current;
                break;
            }
            
            // Проверяем всех соседей (8 направлений)
            for (int i = 0; i < 8; i++) {
                int newX = current.x + DX[i];
                int newY = current.y + DY[i];
                
                if (isValid(newX, newY) && !visited[newX][newY]) {
                    String key = newX + "," + newY;
                    // Пропускаем занятые клетки (кроме целевой)
                    if (occupied.contains(key) && !(newX == targetX && newY == targetY)) {
                        continue;
                    }
                    
                    Cell neighbor = new Cell(newX, newY, current);
                    queue.offer(neighbor);
                    visited[newX][newY] = true;
                    parent[newX][newY] = current;
                }
            }
        }
        
        // Если путь не найден
        if (targetCell == null) {
            return new ArrayList<>();
        }
        
        // Восстанавливаем путь от цели к началу
        List<Edge> path = new ArrayList<>();
        Cell current = targetCell;
        
        // Сложность: O(длина пути) <= O(WIDTH * HEIGHT)
        while (current != null) {
            path.add(0, new Edge(current.x, current.y)); // Добавляем в начало
            current = current.parent;
        }
        
        // Общая сложность: O(WIDTH * HEIGHT), что соответствует требованию
        return path;
    }
    
    private boolean isValid(int x, int y) {
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT;
    }
}
