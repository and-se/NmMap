public interface MtStorage {
    /**
     * Возвращает жизнеописание по ключу
     * @param id ключ
     * @return Human
     */
    Human getHuman(int id);
    
    /**
     * Производит поиск по реквизиту ФИО и возвращает take записей, пропустив первые skip записей
     * @param fioStarts с каких букв должен начинаться реквизит ФИО
     * @param fioContains какие буквы должен содержать реквизит ФИО
     * @param skip сколько записей от начала списка результатов пропустить
     * @param take сколько записей вернуть
     * @return массив записей размера take
     */
    Human[] findHumansByFio(String fioStarts, String fioContains, int skip, int take);
    
    /**
     * Выполняет полнотекстовый поиск жизнеописаний
     * @param query запрос
     * @param skip сколько записей от начала списка результатов пропустить
     * @param take сколько записей вернуть
     * @return массив записей размера take
     */
    Human[] findHumansFullText(String query, int skip, int take);    
}