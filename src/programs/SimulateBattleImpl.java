package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SimulateBattleImpl implements SimulateBattle {
    private PrintBattleLog printBattleLog; // Позволяет логировать. Использовать после каждой атаки юнита

    @Override
    public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {
        if (playerArmy == null || computerArmy == null) {
            return;
        }
        
        // Продолжаем бой, пока в обеих армиях есть живые юниты
        // Сложность: O(R), где R - количество раундов
        while (hasAliveUnits(playerArmy) && hasAliveUnits(computerArmy)) {
            // Собираем всех живых юнитов из обеих армий и сортируем по атаке
            // Сложность: O(N * log(N)), где N - общее количество юнитов
            List<Unit> allUnits = getSortedAliveUnits(playerArmy, computerArmy);
            
            if (allUnits.isEmpty()) {
                break;
            }
            
            // Выполняем ходы по очереди
            // Сложность: O(N^2) в худшем случае, так как может потребоваться пересчет очереди
            int index = 0;
            while (index < allUnits.size()) {
                // Проверяем, есть ли еще живые юниты в обеих армиях
                if (!hasAliveUnits(playerArmy) || !hasAliveUnits(computerArmy)) {
                    break;
                }
                
                // Получаем текущего юнита
                Unit unit = allUnits.get(index);
                
                // Проверяем, не погиб ли юнит до своего хода
                // Если погиб, пересчитываем очередь
                if (!unit.isAlive()) {
                    allUnits = getSortedAliveUnits(playerArmy, computerArmy);
                    if (allUnits.isEmpty() || index >= allUnits.size()) {
                        break;
                    }
                    unit = allUnits.get(index);
                    // Если новый юнит тоже мертв, пропускаем его
                    if (!unit.isAlive()) {
                        index++;
                        continue;
                    }
                }
                
                // Юнит атакует
                // Сложность метода attack(): O(N) согласно заданию
                Unit target = unit.getProgram().attack();
                
                // Логируем атаку
                if (printBattleLog != null) {
                    printBattleLog.printBattleLog(unit, target);
                }
                
                // Если юнит или цель погибли, пересчитываем очередь
                if (!unit.isAlive() || (target != null && !target.isAlive())) {
                    allUnits = getSortedAliveUnits(playerArmy, computerArmy);
                    if (allUnits.isEmpty()) {
                        break;
                    }
                    // Начинаем с начала очереди после пересчета
                    index = 0;
                } else {
                    index++;
                }
            }
            
            // Удаляем погибших юнитов из армий
            // Сложность: O(N)
            if (playerArmy.getUnits() != null) {
                playerArmy.getUnits().removeIf(unit -> !unit.isAlive());
            }
            if (computerArmy.getUnits() != null) {
                computerArmy.getUnits().removeIf(unit -> !unit.isAlive());
            }
        }
        
        // Общая сложность: O(R * (N*log(N) + N*N)) = O(R * N^2)
        // где R - количество раундов, N - количество юнитов
        // Это соответствует требованию O(R * N^2) или лучше
    }
    
    private List<Unit> getSortedAliveUnits(Army playerArmy, Army computerArmy) {
        List<Unit> allUnits = new ArrayList<>();
        
        if (playerArmy.getUnits() != null) {
            for (Unit unit : playerArmy.getUnits()) {
                if (unit != null && unit.isAlive()) {
                    allUnits.add(unit);
                }
            }
        }
        
        if (computerArmy.getUnits() != null) {
            for (Unit unit : computerArmy.getUnits()) {
                if (unit != null && unit.isAlive()) {
                    allUnits.add(unit);
                }
            }
        }
        
        // Сортируем по убыванию атаки
        allUnits.sort(Comparator.comparingInt(Unit::getBaseAttack).reversed());
        
        return allUnits;
    }
    
    private boolean hasAliveUnits(Army army) {
        if (army == null || army.getUnits() == null) {
            return false;
        }
        for (Unit unit : army.getUnits()) {
            if (unit != null && unit.isAlive()) {
                return true;
            }
        }
        return false;
    }
}