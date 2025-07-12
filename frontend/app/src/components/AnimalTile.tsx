import { useNavigate } from 'react-router-dom'
import '../assets/AnimalTile.css'

export interface Animal {
  id: number
  name: string
  species: string
  breed: string
  age: number
  description: string
  imageData?: string
}

interface AnimalTileProps {
  animal: Animal
}

export default function AnimalTile({ animal }: AnimalTileProps) {
  const navigate = useNavigate()
  const hasImage = animal.imageData != null
  const imgSrc = hasImage
    ? `data:image/jpeg;base64,${animal.imageData}`
    : undefined

  return (
    <li
      className="animal-tile"
      onClick={() => navigate(`/animals/${animal.id}`)}
    >
      {hasImage ? (
        <div className="animal-tile__image-wrapper">
          <img
            className="animal-tile__image"
            src={imgSrc}
            alt={animal.name}
          />
        </div>
      ) : (
        <div className="animal-tile__no-image">Brak zdjęcia</div>
      )}
      <div className="animal-tile__info">
        <h3 className="animal-tile__name">{animal.name}</h3>
        <p className="animal-tile__species">Gatunek: {animal.species}</p>
        <p className="animal-tile__age">Wiek: {animal.age} lat</p>
        <p className="animal-tile__description">{animal.description}</p>
      </div>
      <div className="animal-tile__footer" />
    </li>
  )
}
