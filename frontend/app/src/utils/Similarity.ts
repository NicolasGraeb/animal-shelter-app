enum speciesEnum {
  pies   = 0,
  kot    = 0.5,
  ptak   = 1,
}

enum habitEnum {
  INDOOR   = 0,
  CALM     = 0.5,
  FRIENDLY = 1,
}

type InputAnimalCharacteristics = {
  species: speciesEnum;
  age:     number;
  habit:   habitEnum;
};

/**
 * Normalizuje wartość age do przedziału [0,1]
 */
function normalizeAge(age: number, ages: number[]): number {
  const min = Math.min(...ages);
  const max = Math.max(...ages);
  if (max === min) return 0;  // obrona przed dzieleniem przez zero
  return (age - min) / (max - min);
}

/**
 * Oblicza euklidesową odległość dwóch zwierzaków
 */
function calcDistance(
  a: InputAnimalCharacteristics,
  b: InputAnimalCharacteristics,
  ages: number[]
): number {
  // policzemy najpierw znormalizowane wieki, ale *nie* nadpisujemy a.age ani b.age
  const ageA = normalizeAge(a.age, ages);
  const ageB = normalizeAge(b.age, ages);

  const dAge     = Math.abs(ageA     - ageB);
  const dSpecies = Math.abs(a.species - b.species);
  const dHabit   = Math.abs(a.habit   - b.habit);

  return Math.sqrt(
    dAge     * dAge     +
    dSpecies * dSpecies +
    dHabit   * dHabit
  );
}

/**
 * Zwraca listę obiektów { animal, distance }, posortowaną po rosnącej odległości.
 * @param input – zwierzak, względem którego liczymy
 * @param animals – lista kandydatów
 * @param topN – ile pierwszych wyników zwrócić (domyślnie: cała lista)
 */
function getMostSimilar(
  input: InputAnimalCharacteristics,
  animals: InputAnimalCharacteristics[],
  topN = animals.length
): Array<{ animal: InputAnimalCharacteristics; distance: number }> {
  // zbieramy wszystkie wieki do normalizacji (input + kandydaci)
  const ages = [input.age, ...animals.map(x => x.age)];

  const results = animals
    .map(animal => ({
      animal,
      distance: calcDistance(input, animal, ages),
    }))
    .sort((r1, r2) => r1.distance - r2.distance)
    .slice(0, topN);

  return results;
}

// —— Przykład użycia ——

const me: InputAnimalCharacteristics = {
  species: speciesEnum.kot,
  age:     2,
  habit:   habitEnum.FRIENDLY,
};

const zoo: InputAnimalCharacteristics[] = [
  { species: speciesEnum.ptak, age: 13, habit: habitEnum.CALM },
  { species: speciesEnum.pies, age:  5, habit: habitEnum.FRIENDLY },
  { species: speciesEnum.kot,  age:  8, habit: habitEnum.INDOOR },
  { species: speciesEnum.kot,  age:  2, habit: habitEnum.FRIENDLY },
];

// zwróci tablicę 4 wyników; zmień topN, żeby mieć np. top-1 lub top-2
const nearest = getMostSimilar(me, zoo, 3);
nearest.forEach((r, i) => {
  console.log(`${i+1}. odległość = ${r.distance.toFixed(3)}`, r.animal);
});
