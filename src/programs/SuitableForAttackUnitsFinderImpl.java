package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.SuitableForAttackUnitsFinder;

import java.util.ArrayList;
import java.util.List;

public class SuitableForAttackUnitsFinderImpl implements SuitableForAttackUnitsFinder {

    @Override
    public List<Unit> getSuitableUnits(List<List<Unit>> unitsByRow, boolean isLeftArmyTarget) {
        List<Unit> suitableUnits = new ArrayList<>();
        
        // Проходим по каждому ряду
        // Сложность: O(R), где R - количество рядов (3)
        for (List<Unit> row : unitsByRow) {
            if (row == null || row.isEmpty()) {
                continue;
            }
            
            // Для каждого ряда находим крайний юнит
            // Если isLeftArmyTarget = true: атакует компьютер, цели - игрок (не закрыты справа)
            // Если isLeftArmyTarget = false: атакует игрок, цели - компьютер (не закрыты слева)
            
            // Находим крайний юнит в ряду
            Unit extremeUnit = null;
            int extremeY = isLeftArmyTarget ? Integer.MIN_VALUE : Integer.MAX_VALUE;
            
            // Сложность: O(M), где M - количество юнитов в ряду
            for (Unit unit : row) {
                if (unit == null || !unit.isAlive()) {
                    continue;
                }
                
                int unitY = unit.getyCoordinate();
                
                if (isLeftArmyTarget) {
                    // Ищем самый правый юнит (максимальный y)
                    if (unitY > extremeY) {
                        extremeY = unitY;
                        extremeUnit = unit;
                    }
                } else {
                    // Ищем самый левый юнит (минимальный y)
                    if (unitY < extremeY) {
                        extremeY = unitY;
                        extremeUnit = unit;
                    }
                }
            }
            
            if (extremeUnit != null) {
                suitableUnits.add(extremeUnit);
            }
        }
        
        // Общая сложность: O(R * M) = O(M * R), где R = 3 (фиксировано)
        // Фактически O(M), что соответствует требованию O(M * R) или лучше
        return suitableUnits;
    }
}
