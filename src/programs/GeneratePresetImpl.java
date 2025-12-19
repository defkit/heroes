package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;

import java.util.*;

public class GeneratePresetImpl implements GeneratePreset {

    // Класс для хранения информации о типе юнита и его эффективности
    private static class UnitTypeInfo {
        Unit template;
        double attackEfficiency; // атака / стоимость
        double healthEfficiency; // здоровье / стоимость
        int count; // количество добавленных юнитов этого типа
        
        UnitTypeInfo(Unit template) {
            this.template = template;
            this.attackEfficiency = (double) template.getBaseAttack() / template.getCost();
            this.healthEfficiency = (double) template.getHealth() / template.getCost();
            this.count = 0;
        }
    }

    @Override
    public Army generate(List<Unit> unitList, int maxPoints) {
        if (unitList == null || unitList.isEmpty()) {
            return new Army(new ArrayList<>());
        }
        
        // Создаем список информации о типах юнитов
        // Сложность: O(T), где T - количество типов юнитов
        List<UnitTypeInfo> unitTypes = new ArrayList<>();
        for (Unit unit : unitList) {
            if (unit != null) {
                unitTypes.add(new UnitTypeInfo(unit));
            }
        }
        
        // Сортируем по эффективности: сначала по атаке/стоимость, затем по здоровье/стоимость
        // Сложность: O(T * log(T)), но T обычно мало (4 типа), так что можно считать O(T)
        unitTypes.sort((a, b) -> {
            int attackCompare = Double.compare(b.attackEfficiency, a.attackEfficiency);
            if (attackCompare != 0) {
                return attackCompare;
            }
            return Double.compare(b.healthEfficiency, a.healthEfficiency);
        });
        
        List<Unit> armyUnits = new ArrayList<>();
        int totalPoints = 0;
        final int MAX_UNITS_PER_TYPE = 11;
        
        // Жадный алгоритм: добавляем юниты по приоритету эффективности
        // Сложность: O(T * N), где N - максимальное количество юнитов в армии
        boolean added = true;
        while (added) {
            added = false;
            
            for (UnitTypeInfo typeInfo : unitTypes) {
                // Проверяем ограничения
                if (typeInfo.count >= MAX_UNITS_PER_TYPE) {
                    continue;
                }
                
                int unitCost = typeInfo.template.getCost();
                if (totalPoints + unitCost > maxPoints) {
                    continue;
                }
                
                // Создаем новый юнит того же типа
                // Используем конструктор Unit для создания копии
                Unit newUnit = createUnitCopy(typeInfo.template, armyUnits.size());
                armyUnits.add(newUnit);
                totalPoints += unitCost;
                typeInfo.count++;
                added = true;
                break; // Начинаем с начала списка для следующей итерации
            }
        }
        
        Army army = new Army(armyUnits);
        army.setPoints(totalPoints);
        return army;
    }
    
    // Вспомогательный метод для создания копии юнита
    private Unit createUnitCopy(Unit template, int index) {
        // Создаем новый юнит с теми же характеристиками
        // Используем конструктор Unit(String name, String unitType, int health, 
        // int baseAttack, int cost, String attackType, ...)
        String name = template.getUnitType() + "_" + index;
        return new Unit(
            name,
            template.getUnitType(),
            template.getHealth(),
            template.getBaseAttack(),
            template.getCost(),
            template.getAttackType(),
            template.getAttackBonuses(),
            template.getDefenceBonuses(),
            0, // x координата
            0  // y координата
        );
    }
}