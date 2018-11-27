import React, {Component} from 'react'
import axios from 'axios'

const UPLOAD_URL = "http://localhost:9000/upload"

/**
 * this component handles file upload in front and provides response
 */
class FileUpload extends Component {
    constructor(props) {
        super(props)

        this.state = {
            fileName: '',
            file: '',
            uploadResponse: ''
        }

        this.handleImageChange = this.handleImageChange.bind(this)
        this.uploadFile = this.uploadFile.bind(this)
    }

    handleImageChange(e) {
        e.preventDefault()
        const imgFile = e.target.files[0]
        this.setState({file: imgFile, fileName: URL.createObjectURL(imgFile), uploadResponse: ''})
    }

    uploadFile() {
        let fd = new FormData()
        fd.append("image", this.state.file)
        axios.post(UPLOAD_URL, fd).then(response => response.status === 200 ? this.setState({uploadResponse: 'file has been uploaded'}) : this.setState({uploadResponse: 'cannot upload file'}))
    }

    render() {
        return <div>
            <div>Select file to upload</div>
            <div><a href={""}>View article here</a></div>
            <div><input type="file" id="image4" onChange={this.handleImageChange}/></div>
            <button onClick={this.uploadFile} disabled={this.state.file === ''}>Upload</button>
            <h3>{this.state.uploadResponse}</h3>
            <img src={this.state.fileName}/>
        </div>
    }
}

export default FileUpload

