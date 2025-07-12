import React, { useEffect, useState } from 'react'
import { useApi } from '../hooks/useApi'
import AnimalTile from '../components/AnimalTile'

import '../assets/Adopt.css'

export interface Animal {
  id: number
  name: string
  species: string
  breed: string
  age: number
  description: string
  imageData?: string
}

enum speciesEnum {
    pies =0,
    kot =0.5,
    ptak =1
}

enum habitEnum{
    INDOOR=0,
    CALM=0.5,
    FRIENDLY=1,
}

type InputAnimalCharacteristics = {
    species: speciesEnum,
    age: number,
    habit: habitEnum,
}

export default function Adopt() {
  const api = useApi()
  const [animals, setAnimals] = useState<Animal[]>([])
  const [filterSpecies, setFilterSpecies] = useState<string>('')
  const [ageSort, setAgeSort] = useState<'none' | 'asc' | 'desc'>('none')

  useEffect(() => {
    api
      .get('/animals')
      .then((data: Animal[]) => setAnimals(data))
      .catch(console.error)
  }, [])

  const handleSpeciesChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFilterSpecies(e.target.value)
  }

  const handleAgeSortChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setAgeSort(e.target.value as 'none' | 'asc' | 'desc')
  }

  const filtered = animals
    .filter(a => a.species.toLowerCase().includes(filterSpecies.trim().toLowerCase()))
  const sorted = [...filtered]
  if (ageSort === 'asc') {
    sorted.sort((a, b) => a.age - b.age)
  } else if (ageSort === 'desc') {
    sorted.sort((a, b) => b.age - a.age)
  }

  //const calcSimilarity = (a: InputAnimalCharacteristics, animals: )

  return (
    <div className="adopt-page">
      <h2>Dostępne zwierzaki do adopcji</h2>

      <div className="filters">
        <input
          type="text"
          placeholder="Filtruj po gatunku"
          value={filterSpecies}
          onChange={handleSpeciesChange}
        />
        <select value={ageSort} onChange={handleAgeSortChange}>
          <option value="none">Sortuj wg wieku</option>
          <option value="asc">Wiekszy wiek ↑</option>
          <option value="desc">Wiekszy wiek ↓</option>
        </select>
      </div>

      <ul className="animal-list">
        {sorted.map(animal => (
          <AnimalTile key={animal.id} animal={animal} />
        ))}
      </ul>
    </div>
  )
}
