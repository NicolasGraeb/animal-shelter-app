import React, { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { useApi } from '../hooks/useApi'
import { Star, Edit3, Trash2 } from 'lucide-react'
import AnimalEditModal from '../components/AnimalEditModal'
import '../assets/AnimalDetails.css'

export interface Animal {
  id: number
  name: string
  species: string
  breed: string
  age: number
  description: string
  status?: string
  imageData?: string
}

export default function AnimalDetails() {
  const { id } = useParams<{ id: string }>()
  const api = useApi()

  const [animal, setAnimal]      = useState<Animal | null>(null)
  const [liked, setLiked]        = useState(false)
  const [likesLoaded, setLoaded] = useState(false)
  const [isToggling, setToggling]= useState(false)

  const [isAdmin, setIsAdmin]    = useState(false)
  const [editing, setEditing]    = useState<Animal | null>(null)
  const [modalVisible, setModalVisible] = useState(false)

  useEffect(() => {
    api.get('/users/me')
      .then((u: any) => setIsAdmin(u.role === 'ADMIN'))
      .catch(console.error)

    api.get<Animal>(`/animals/${id}`)
      .then(setAnimal)
      .catch(console.error)
  }, [id])

  useEffect(() => {
    api.get<{ liked: boolean }>(`/favorites/exists/${id}`)
      .then(res => setLiked(res.liked))
      .catch(console.error)
      .finally(() => setLoaded(true))
  }, [id])

  if (!animal) return <div>Ładowanie...</div>

  const imgSrc = animal.imageData
    ? `data:image/jpeg;base64,${animal.imageData}`
    : undefined

  const handleAdopt = async () => {
    try {
      await api.post(`/animals/${animal.id}/adoptions`, {})
      alert('Wysłano wniosek adopcyjny')
    } catch {
      alert('Błąd przy wysyłaniu wniosku')
    }
  }

  const toggleLike = async () => {
    if (isToggling) return
    setToggling(true)
    try {
      if (!liked) {
        await api.post('/favorites', { animalId: animal.id })
        setLiked(true)
      } else {
        await api.del(`/favorites/${animal.id}`)
        setLiked(false)
      }
    } catch {
      alert('Błąd przy aktualizacji ulubionych')
    } finally {
      setToggling(false)
    }
  }

  const handleEdit = () => {
    setEditing(animal)
    setModalVisible(true)
  }

  const handleDelete = async () => {
    if (!confirm('Na pewno usunąć zwierzaka?')) return
    try {
      await api.del(`/animals/${animal.id}`)
      alert('Usunięto zwierzaka')
      window.location.href = '/'
    } catch {
      alert('Błąd przy usuwaniu')
    }
  }

  const handleSave = async (updated: Animal) => {
    try {
      await api.put(`/animals/${updated.id}`, updated)
      setAnimal(updated)
      setModalVisible(false)
      alert('Zapisano zmiany')
    } catch {
      alert('Błąd przy zapisie zmian')
    }
  }

  return (
    <div className="animal-details">
      {imgSrc && <img className="animal-details__image" src={imgSrc} alt={animal.name} />}
      <h2 className="animal-details__name">{animal.name}</h2>
      <p className="animal-details__species">Gatunek: {animal.species}</p>
      <p className="animal-details__breed">Rasa: {animal.breed}</p>
      <p className="animal-details__age">Wiek: {animal.age} lat</p>
      <p className="animal-details__description">{animal.description}</p>

      <div className="animal-details__actions">
        <button onClick={handleAdopt} className="adopt-button">
          Adoptuj
        </button>

        {likesLoaded && (
          <Star
            className={`animal-details__star ${liked ? 'animal-details__star--liked' : ''} ${isToggling ? 'animal-details__star--disabled' : ''}`}
            onClick={toggleLike}
          />
        )}

        {isAdmin && (
          <>
            <Edit3
              className="animal-details__admin-icon"
              onClick={handleEdit}
              title="Edytuj zwierzaka"
            />
            <Trash2
              className="animal-details__admin-icon"
              onClick={handleDelete}
              title="Usuń zwierzaka"
            />
          </>
        )}
      </div>

      <AnimalEditModal
        animal={editing}
        visible={modalVisible}
        onClose={() => setModalVisible(false)}
        onSave={handleSave}
      />
    </div>
  )
}
