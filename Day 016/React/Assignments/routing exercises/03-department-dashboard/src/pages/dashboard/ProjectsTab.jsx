import { useOutletContext } from 'react-router-dom'

function ProjectsTab() {
  const { projects } = useOutletContext()
  return (
    <div>
      <h2 className="tab-title">Projects</h2>
      {projects.length === 0 ? (
        <p className="muted">No projects in this department.</p>
      ) : (
        <ul className="tab-list">
          {projects.map((project) => (
            <li key={project.id}>{project.name} <span className="muted">— {project.status}</span></li>
          ))}
        </ul>
      )}
    </div>
  )
}

export default ProjectsTab
